package com.wecode.base;

/**
 * 返回类型信息
 * @author wecode
 */
public enum MessageInfo {

    SUCCESS(200, "操作成功"),
    NOTLOGIN(405, "用户还未登录"),
    FAILURE(500, "操作失败;failure"),
    ;

    private int code;
    private String msg;

    MessageInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
