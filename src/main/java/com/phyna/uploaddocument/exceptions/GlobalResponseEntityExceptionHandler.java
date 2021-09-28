package com.phyna.uploaddocument.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.phyna.uploaddocument.base.ApiResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

@Primary
@RestControllerAdvice
@Slf4j
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ApiResponseBase<?>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.debug(ex.getLocalizedMessage(), ex);
        ex.printStackTrace();
        ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
        apiResponseBase.setHasError(true);
        apiResponseBase.setErrorMessage(getErrorMessage(ex) +": Maximum 5MB");
        return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(value = {FileException.class}) //if this <<<<== then remove the extends above
    @ExceptionHandler
    protected ResponseEntity<ApiResponseBase<?>> handleGeneralException(FileException ex) {
        log.debug(ex.getLocalizedMessage(), ex);
        ex.printStackTrace();
        ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
        apiResponseBase.setHasError(true);
        apiResponseBase.setErrorMessage(getErrorMessage(ex));
        return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ApiResponseBase<?>> handleGeneralRuntimeException(FileRuntimeException ex) {
        log.debug(ex.getLocalizedMessage(), ex);
        ex.printStackTrace();
        ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
        apiResponseBase.setHasError(true);
        apiResponseBase.setErrorMessage(getErrorMessage(ex));
        return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(value = {ProcessException.class})
    @ExceptionHandler
    protected ResponseEntity<ApiResponseBase<?>> processException(ProcessException ex) {
        log.debug(ex.getLocalizedMessage(), ex);
        ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
        apiResponseBase.setHasError(true);
        apiResponseBase.setErrorCode(ex.getErrorCode());
        apiResponseBase.setErrorMessage(getErrorMessage(ex));
        log.info(apiResponseBase.getErrorMessage(), ex.fillInStackTrace());
        return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(value = {
            Exception.class,
            RuntimeException.class
    })
    protected ResponseEntity<ApiResponseBase<?>> unknownGeneralException(Exception ex) {

        ex.printStackTrace();

        if (ex.getCause() instanceof InvalidClassException) {
            return handleGeneralException(new FileException(ex.getCause().getMessage()));

        } else if (ex instanceof FileRuntimeException) {
            return handleGeneralRuntimeException(new FileRuntimeException(ex.getCause().getMessage()));
        } else if (ex instanceof InvocationTargetException) {
            return handleGeneralRuntimeException(new FileRuntimeException(ex));
        } else if (ex.getCause() instanceof FileException) {
            return handleGeneralException(new FileException(ex.getCause().getMessage()));
        } else if (ex instanceof FileException) {
            return handleGeneralException(new FileException(ex.getMessage()));
        }


        ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
        apiResponseBase.setHasError(true);
        apiResponseBase.setErrorMessage(getErrorMessage(ex));
        log.warn(apiResponseBase.getErrorMessage(), ex.fillInStackTrace());
        log.debug(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            Throwable.class
    })
    protected ResponseEntity<ApiResponseBase<?>> throwable(Throwable e) {
        log.debug(e.getLocalizedMessage(), e);

        if (e instanceof FileException) {

            FileException ex = ((FileException) e);
            log.info("IconException", ex);

            ex.printStackTrace();
            ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
            apiResponseBase.setHasError(true);
            apiResponseBase.setErrorMessage(getErrorMessage(ex));
            return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
        } else if (e instanceof InvocationTargetException) {

            InvocationTargetException ex = ((InvocationTargetException) e);
            log.info("InvocationTargetException", ex.getCause());

            ex.printStackTrace();
            ApiResponseBase<?> apiResponseBase = new ApiResponseBase<>();
            apiResponseBase.setHasError(true);
            apiResponseBase.setErrorMessage(ex.getCause().getMessage());
            return new ResponseEntity<>(apiResponseBase, HttpStatus.BAD_REQUEST);
        }
        log.info("Unknown Throwable", e);

        return null;
    }

    private String getErrorMessage(Exception ex) {
        String unhandledErrMsg = "An internal error occurred while processing your request";
        String genericIconErrMsg = "Invalid request value entered";
        String errMsg = "";
        boolean isKnown = ex instanceof FileException;

        if (isKnown) {
            errMsg = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : genericIconErrMsg;
        } else {
            for (Throwable throwable = ex.getCause(); throwable != null; throwable = throwable.getCause()) {
                // General Json
                if (throwable instanceof JsonMappingException) {
                    errMsg = getJsonErrorMsg((JsonMappingException) throwable);

                    break;
                }

                // DATE
                if (throwable instanceof DateTimeParseException) {
                    errMsg = getDateErrorMsg(throwable);

                    break;
                }

                if (throwable instanceof InvalidFormatException) {

                    try {
                        Class<?> type = ((InvalidFormatException) throwable).getTargetType();

                        // ENUM
                        if (type instanceof Class && type.isEnum()) {
                            errMsg = getEnumErrorMsg(errMsg, (InvalidFormatException) throwable, type);
                        }

                        break;
                    } catch (Exception e) {

                    }
                }
            }

            errMsg = StringUtils.hasText(errMsg) ? errMsg : unhandledErrMsg;
        }

        return errMsg;
    }

    private String getEnumErrorMsg(String errMsg, InvalidFormatException throwable, Class<?> type) {
        Object value = throwable.getValue();
        List<JsonMappingException.Reference> path = throwable.getPath();
        AtomicReference<String> fieldName = new AtomicReference<>("");
        path.forEach(reference -> fieldName.set(reference.getFieldName()));

        Object[] enums = type.getEnumConstants();
        StringJoiner stringJoiner = new StringJoiner(",");
        for (Object enumName : enums) {
            stringJoiner.add(enumName.toString());
        }
        errMsg = String.format("Wrong value '%s' for '%s', expected values are [%s]", value, fieldName.get(), stringJoiner.toString());
        return errMsg;
    }

    private String getDateErrorMsg(Throwable throwable) {
        String errMsg;
        errMsg = String.format("Wrong Date/Time format %s", throwable.getMessage().substring(throwable.getMessage().indexOf("'"), throwable.getMessage().lastIndexOf("'")));
        return errMsg;
    }


    private String getJsonErrorMsg(JsonMappingException jsonMappingException) {

        return String.format("Wrong input format: %s", jsonMappingException.getMessage().substring(0, jsonMappingException.getMessage().indexOf("):")));
    }
}
