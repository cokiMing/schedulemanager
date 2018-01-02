package com.cokiming.common.framework;

import org.springframework.core.NamedThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/1/2.
 */
public class HttpContextHolder {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal = new NamedThreadLocal<>("httpServletRequest");

    private static ThreadLocal<HttpServletResponse> responseThreadLocal = new NamedThreadLocal<>("httpServletResponse");

    public static void setRequest(HttpServletRequest request) {
        requestThreadLocal.set(request);
    }

    public static HttpServletRequest getRequest() {
        return requestThreadLocal.get();
    }

    public static void removeRequest() {
        requestThreadLocal.remove();
    }

    public static void setResponse(HttpServletResponse response) {
        responseThreadLocal.set(response);
    }

    public static HttpServletResponse getResponse() {
        return responseThreadLocal.get();
    }

    public static void removeResponse() {
        responseThreadLocal.remove();
    }
}
