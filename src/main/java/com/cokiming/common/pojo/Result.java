package com.cokiming.common.pojo;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/9/14.
 */
public final class Result extends JSONObject {

    private final static String STATUS = "code";
    private final static String MESSAGE = "message";
    private final static String CONTENT = "content";

    private final static String SUCCESS_CODE = "1";
    private final static String FAIL_CODE = "0";
    private final static String ERROR_CODE = "-1";

    private final static String SUCCESS_MESSAGE = "success";
    private final static String FAIL_MESSAGE = "illegal request";
    private final static String ERROR_MESSAGE = "system error";

    public static Result success() {
        return getResult(SUCCESS_CODE,SUCCESS_MESSAGE,null);
    }

    public static Result success(Object content) {
        return getResult(SUCCESS_CODE,SUCCESS_MESSAGE,content);
    }

    public static Result fail() {
        return getResult(FAIL_CODE,FAIL_MESSAGE,null);
    }

    public static Result fail(String message) {
        return getResult(FAIL_CODE,message,null);
    }

    public static Result error() {
        return getResult(ERROR_CODE,ERROR_MESSAGE,null);
    }

    public static Result error(String message) {
        return getResult(ERROR_CODE,message,null);
    }

    public static Result customResult(String code,String message,Object content) {
        return getResult(code,message,content);
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.getString(STATUS));
    }

    private Result() {
    }

    private static Result getResult(String code, String message, Object content) {
        Result result = new Result();
        result.put(STATUS,code);
        result.put(MESSAGE,message);
        result.put(CONTENT,content);
        return result;
    }

}
