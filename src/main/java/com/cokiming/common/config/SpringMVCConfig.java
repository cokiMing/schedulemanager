package com.cokiming.common.config;

import com.cokiming.common.interceptor.HttpInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/1/2.
 */
@Component
public class SpringMVCConfig extends WebMvcConfigurerAdapter {

    /**
     * 拦截器加载
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpInterceptor()).addPathPatterns("/**");
    }
}
