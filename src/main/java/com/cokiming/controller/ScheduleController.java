package com.cokiming.controller;

import com.alibaba.fastjson.JSONObject;
import com.cokiming.common.pojo.Result;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import com.cokiming.service.ScheduleManager;
import com.cokiming.service.ScheduleService;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@RestController
public class ScheduleController {

    @Autowired
    private ScheduleManager scheduleManager;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 创建一个预定时间点执行的任务
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/createSchedule",method = RequestMethod.POST)
    public Result createScheduleTask(@RequestBody JSONObject jsonObject) {
        String url = jsonObject.getString("url");
        String method = jsonObject.getString("method");
        Date targetTime = jsonObject.getDate("targetTime");
        String project = jsonObject.getString("project");

        if (targetTime.before(new Date())) {
            return Result.fail("预定执行时间应当晚于当前");
        }
        return scheduleManager.createSchedule(url,method,targetTime,project);
    }

    /**
     * 创建一个定时任务
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/createScheduleByCron",method = RequestMethod.POST)
    public Result createScheduleByCron(@RequestBody JSONObject jsonObject) {
        String cron = jsonObject.getString("cronExpression");
        if (!checkCronExpression(cron)) {
            return Result.fail("cron表达式格式不正确");
        }

        String url = jsonObject.getString("url");
        String method = jsonObject.getString("method");
        String project = jsonObject.getString("project");

        return scheduleManager.createSchedule(url,method,cron,project);
    }

    /**
     * 获取活动中的定时任务
     * @return
     */
    @RequestMapping(value = "/getAvailableJobs",method = RequestMethod.GET)
    public Result getAvailableJobs() {
        return Result.success(scheduleService.selectAvailableJobs());
    }

    /**
     * 获取定时任务的运行日志
     * @return
     */
    @RequestMapping(value = "/getJobLogsByJobName",method = RequestMethod.GET)
    public Result getJobLogsById(@RequestParam String jobName,
                                 @RequestParam(defaultValue = "1") int pageNo,
                                 @RequestParam(defaultValue = "20") int pageSize) {
        List<ScheduleLog> logs = scheduleService.selectLogsByPage(jobName, pageNo, pageSize);
        return Result.success(logs);
    }

    /**
     * 移除定时任务
     * @return
     */
    @RequestMapping(value = "/removeSchedule",method = RequestMethod.DELETE)
    public Result deleteSchedule(@RequestParam String id) {
        if (id == null) {
            return Result.fail("id为空");
        }
        ScheduleJob scheduleJob = scheduleService.selectJobById(id);
        if (scheduleJob == null) {
            return Result.fail("没有找到定时任务");
        }

        return scheduleManager.removeSchedule(scheduleJob.getJobName(),scheduleJob.getProject());
    }

    private boolean checkCronExpression(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException e) {

        }

        return false;
    }
}
