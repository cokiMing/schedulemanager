package com.cokiming.dao.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Entity(noClassnameStored = true)
public class ScheduleLog {
    @Id
    /**定时任务id*/
    private String id;
    /**执行时间*/
    private Date executeTime;
    /**执行结果*/
    private String executeResult;
    /**异常原因*/
    private String exception;
    /**接口或方法返回内容*/
    private String returnContent;
    /**jobName*/
    private String jobName;
    /**方法名*/
    private String methodName;
    /**连续失败次数*/
    private int failTimes;

    public static final String RESULT_SUCCESS = "SUCCESS";

    public static final String RESULT_FAIL = "FAIL";

    public static final int DEFAULT_MAX_FAIL_TIMES = 5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getReturnContent() {
        return returnContent;
    }

    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getFailTimes() {
        return failTimes;
    }

    public void setFailTimes(int failTimes) {
        this.failTimes = failTimes;
    }

    @Override
    public String toString() {
        return "ScheduleLog{" +
                "id='" + id + '\'' +
                ", executeTime=" + executeTime +
                ", executeResult='" + executeResult + '\'' +
                ", exception='" + exception + '\'' +
                ", returnContent='" + returnContent + '\'' +
                ", jobName='" + jobName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
