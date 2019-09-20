package com.zk.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2002,"问题没有找到"),
    TARGET_PARAMENT_NOT_FOUND(2003,"没有选中问题"),
    NO_LOGIN_IN(2004,"未登录，请先登录"),
    SYSTEM_ERROR(4004,"系统错误"),
    TARGET_PARAM_NOT_FOUND(2005,"评论类型不存在"),
    COMMENT_NOT_FOUND(2006,"问题不存在"),
    COMMENT_IS_EMPTY(2007,"评论不能为空"),
    READ_NOTIFICATION_FAIL(2008,"非法操作"),
    NOTIFICATION_NOT_FIND(2009,"非法操作");

    private Integer code;
    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    CustomizeErrorCode(String message) {
        this.message = message;
    }
}
