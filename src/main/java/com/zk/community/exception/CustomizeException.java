package com.zk.community.exception;

public class CustomizeException extends RuntimeException{

    ICustomizeErrorCode customizeErrorCode;
//    private String message;

    public CustomizeException(ICustomizeErrorCode customizeErrorCode) {
       this.customizeErrorCode = customizeErrorCode;
    }

    @Override
    public String getMessage() {
        return customizeErrorCode.getMessage();
    }
}
