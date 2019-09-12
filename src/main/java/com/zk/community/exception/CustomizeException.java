package com.zk.community.exception;

public class CustomizeException extends RuntimeException{

//    ICustomizeErrorCode customizeErrorCode;
    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode customizeErrorCode) {
        this.message = customizeErrorCode.getMessage();
        this.code = customizeErrorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
