package com.alpengotter.dodo_project.handler.exception;

import com.alpengotter.dodo_project.handler.ErrorType;

public class LemonBankException extends RuntimeException{

    public final ErrorType errorType;
    public LemonBankException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    public LemonBankException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }


}
