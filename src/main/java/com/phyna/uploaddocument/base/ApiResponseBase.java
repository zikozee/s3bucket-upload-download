package com.phyna.uploaddocument.base;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApiResponseBase<T>
{

    private T Response;

    private String successMessage;

    private boolean hasError;
    private String errorMessage;
    private String errorCode;

    private String errorScope;
}


