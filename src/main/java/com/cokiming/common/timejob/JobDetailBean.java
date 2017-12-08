package com.cokiming.common.timejob;

import com.cokiming.common.framework.SpringContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
public class JobDetailBean extends QuartzJobBean{

    private Log logger = LogFactory.getLog(JobDetailBean.class);
    private String targetObject;
    private String targetMethod;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Object bean = SpringContext.getBean(targetObject);
            Method m = bean.getClass().getMethod(targetMethod,String.class,String.class,String.class,String.class);
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            String url = jobDataMap.getString("url");
            String project = jobDataMap.getString("project");
            String method = jobDataMap.getString("method");
            String cronExpression = jobDataMap.getString("cronExpression");
            m.invoke(bean,url,method,project,cronExpression);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
}
