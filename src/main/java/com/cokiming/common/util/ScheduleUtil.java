package com.cokiming.common.util;

import com.cokiming.common.framework.SpringContext;
import com.cokiming.common.timejob.JobDetailBean;
import org.quartz.*;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/11/10.
 */
public class ScheduleUtil {

    /**
     * 将日期转化为cron表达式
     * @param date
     * @return
     */
    public static String convertDateToCron(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int min = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DATE);
        int mon = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return "0 "+min+" "+hour+" "+day+" "+mon+" ? "+year;
    }

    /**
     * 创建指定时间的任务
     * @param clazz 执行定时任务的类，它必须在Spring容器中注册并且拥有与类名相同的id
     * @param cronExpression 定时任务执行的目标时间
     * @param jobName 任务名称
     * @throws Exception
     */
    public static void createSchedule(Class clazz,String cronExpression,String jobName,String methodName,String url,String project,String method,String id) throws Exception {
        //该类需要设定与类名相同的bean id
        String className = clazz.getSimpleName();

        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setName(jobName);
        jobDetailFactoryBean.setJobClass(JobDetailBean.class);
        jobDetailFactoryBean.setDurability(false);
        jobDetailFactoryBean.setGroup("scheduleManager");
        Map<String,String> jobDataAsMap = new HashMap<>(5);
        jobDataAsMap.put("targetObject",className);
        jobDataAsMap.put("targetMethod",methodName);
        jobDataAsMap.put("url",url);
        jobDataAsMap.put("project",project);
        jobDataAsMap.put("method",method);
        jobDataAsMap.put("cronExpression",cronExpression);
        jobDataAsMap.put("jobId",id);
        jobDetailFactoryBean.setJobDataAsMap(jobDataAsMap);
        jobDetailFactoryBean.afterPropertiesSet();

        JobDetail jobDetail = jobDetailFactoryBean.getObject();
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, project)
                .withSchedule(scheduleBuilder).build();

        getSchedulerFactory().scheduleJob(jobDetail,trigger);
    }

    /**
     * 移除定时任务
     * @param jobName triggerKey
     * @param group 分组
     * @throws Exception
     */
    public static void removeSchedule(String jobName,String group) throws Exception {
        Scheduler scheduler = getSchedulerFactory();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName,group);
        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
        if(trigger != null){
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(trigger.getJobKey());
        }
    }

    public static String createJobName(String url, String project, String cronExpression) {
        return Md5Util.string2MD5(url + project + cronExpression);
    }

    private static Scheduler getSchedulerFactory() {
        return (Scheduler) SpringContext.getBean("quartzScheduler");
    }
}
