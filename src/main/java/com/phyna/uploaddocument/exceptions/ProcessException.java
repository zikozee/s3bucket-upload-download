package com.phyna.uploaddocument.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProcessException extends FileException {

    private String errorCode;

    public ProcessException(Throwable t)
    {
        super(t);
    }

    public ProcessException(String message) {        super(message);
        this.errorCode = "0";
    }

    public ProcessException(String errorCode,String message)
    {
        super(message);

        this.errorCode=errorCode;
    }
}
