package com.cokiming.common.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wuyiming
 * Created by admin on 16/4/14.
 */
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        httpServletResponse.addHeader("Access-Control-Allow-Headers",
                "Authorization,Version,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,Keep-Alive,X-Requested-With,If-Modified-Since");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials","true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
