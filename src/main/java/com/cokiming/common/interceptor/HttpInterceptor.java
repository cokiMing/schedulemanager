package com.cokiming.common.interceptor;

import com.cokiming.common.framework.HttpContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/1/2.
 */
@Component("httpInterceptor")
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpContextHolder.setRequest(request);
        HttpContextHolder.setResponse(response);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        HttpContextHolder.removeRequest();
        HttpContextHolder.removeResponse();
    }
}
