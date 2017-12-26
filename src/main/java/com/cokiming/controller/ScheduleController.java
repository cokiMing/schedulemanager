package com.cokiming.controller;

import com.alibaba.fastjson.JSONObject;
import com.cokiming.common.pojo.Result;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.service.ScheduleManager;
import com.cokiming.service.ScheduleService;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;

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
        Date targetTime = jsonObject.getDate("targetTime");
        if (targetTime.before(new Date())) {
            return Result.fail("预定执行时间应当晚于当前");
        }

        String url = jsonObject.getString("url");
        if (url == null) {
            return Result.fail("接口地址为空");
        }

        String name = jsonObject.getString("name");
        if (name == null) {
            return Result.fail("任务名称为空");
        }

        String method = jsonObject.getString("method");
        if (method == null) {
            return Result.fail("请求方式为空");
        }

        String project = jsonObject.getString("project");
        if (project == null) {
            return Result.fail("项目为空");
        }

        String description = jsonObject.getString("description");
        return scheduleManager.createSchedule(url,method,targetTime,project,name,description);
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
        if (url == null) {
            return Result.fail("接口地址为空");
        }

        String name = jsonObject.getString("name");
        if (name == null) {
            return Result.fail("任务名称为空");
        }

        String method = jsonObject.getString("method");
        if (method == null) {
            return Result.fail("请求方式为空");
        }

        String project = jsonObject.getString("project");
        if (project == null) {
            return Result.fail("项目为空");
        }

        String description = jsonObject.getString("description");
        return scheduleManager.createSchedule(url,method,cron,project,name,description);
    }

    /**
     * 获取活动中的定时任务
     * @return
     */
    @RequestMapping(value = "/getRunningJobs",method = RequestMethod.GET)
    public Result getAvailableJobs() {
        return Result.success(scheduleService.selectAvailableJobs());
    }

    /**
     * 获取被系统杀死的定时任务
     * @return
     */
    @RequestMapping(value = "/getFiredJobs",method = RequestMethod.GET)
    public Result getFiredJobs() {
        return Result.success(scheduleService.selectFiredJobs());
    }

    /**
     * 获取定时任务的运行日志
     * @return
     */
    @RequestMapping(value = "/getJobLogsByJobId",method = RequestMethod.GET)
    public Result getJobLogsById(@RequestParam String jobId,
                                 @RequestParam(defaultValue = "1") int pageNo,
                                 @RequestParam(defaultValue = "20") int pageSize) {
        return scheduleService.selectLogsByPage(jobId, pageNo, pageSize);
    }

    /**
     * 更新定时任务
     * 也可用于过期任务的恢复
     * @return
     */
    @RequestMapping(value = "/updateJobById",method = RequestMethod.PUT)
    public Result updateJobById(@RequestBody JSONObject requestObject) {
        String id = requestObject.getString("id");
        if (id == null) {
            return Result.fail("id为空");
        }

        String cron = requestObject.getString("cronExpression");
        if (cron == null) {
            Date targetTime = requestObject.getDate("targetTime");
            if (targetTime != null && targetTime.after(new Date())) {
                cron = ScheduleUtil.convertDateToCron(targetTime);
            } else {
                return Result.fail("执行时间异常");
            }
        } else {
            if (!checkCronExpression(cron)) {
                return Result.fail("cron表达式格式不正确");
            }
        }

        String url = requestObject.getString("url");
        if (StringUtils.isEmpty(url)) {
            return Result.fail("url为空");
        }

        ScheduleJob scheduleJob = scheduleService.selectJobById(id);
        if (scheduleJob == null) {
            return Result.fail("没有找到任务");
        }


        String description = requestObject.getString("description");
        return scheduleManager.updateSchedule(
                scheduleJob,
                url,
                cron,
                description
        );
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
