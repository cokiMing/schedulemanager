package com.cokiming.dao.entity;

import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/8.
 */
public class ScheduleJob {
    /**定时任务id*/
    private String id;
    /**接口地址*/
    private String url;
    /**方法名*/
    private String requestMethod;
    /**所属项目*/
    private String project;
    /**任务描述*/
    private String description;
    /**定时任务对应的cron表达式*/
    private String cronExpression;
    /**jobName*/
    private String jobName;
    /**创建时间*/
    private Date createTime;
    /**更新时间*/
    private Date updateTime;
    /**状态*/
    private String status;

    public static final String STATUS_CREATE = "CREATE";

    public static final String STATUS_DELETE = "DELETE";

    public static final String STATUS_FIRED = "FIRED";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ScheduleJob{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", project='" + project + '\'' +
                ", description='" + description + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", jobName='" + jobName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", status='" + status + '\'' +
                '}';
    }
}
