package com.cokiming.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


/**
 * @author wuyiming
 * quartz配置
 * Created by wuyiming on 2017/8/28.
 */
@Configuration
public class SpringScheduleConfig {

    /**
     * quartz核心调度器
     * @return
     */
    @Bean(name = "quartzScheduler")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setOverwriteExistingJobs(true);
        bean.setAutoStartup(true);
        bean.setStartupDelay(5);
        try{
            bean.afterPropertiesSet();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }
}
