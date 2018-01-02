package com.cokiming.common.config;

import com.cokiming.common.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/21.
 */
@Configuration
public class SpringAppConfig {

    /**
     * filter配置
     * @return
     */
    @Bean
    public FilterRegistrationBean appCorsFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CorsFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }


}
