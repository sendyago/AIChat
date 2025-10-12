package com.wecode.base;

/**
 * 返回类型
 * @author wecode
 */
public class ResultInfo<T> {

    private int code = 200;
    private String msg = "Success";
    private T data;

    public ResultInfo() {

    }

    public ResultInfo(T data) {
        this.data = data;
    }

    public ResultInfo(MessageInfo info, T data) {
        this.code = info.getCode();
        this.msg = info.getMsg();
        this.data = data;
    }

    public ResultInfo(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
