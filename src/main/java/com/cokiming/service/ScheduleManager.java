package com.cokiming.service;

import com.cokiming.common.http.HttpUtil;
import com.cokiming.common.pojo.Result;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

import static com.cokiming.common.util.ScheduleUtil.createJobName;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Service("ScheduleManager")
public class ScheduleManager {

    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ScheduleService scheduleService;

    public void startSchedule(ScheduleJob scheduleJob) throws Exception {
        ScheduleUtil.createSchedule(
                this.getClass(),
                scheduleJob.getCronExpression(),
                scheduleJob.getJobName(),
                "executeMethod",
                scheduleJob.getUrl(),
                scheduleJob.getProject(),
                scheduleJob.getRequestMethod()
        );
    }

    public Result createSchedule(String url, String method, Date targetTime, String project) {
        String cron = ScheduleUtil.convertDateToCron(targetTime);
        return createSchedule(url,method,cron,project);
    }

    public Result createSchedule(String url, String method, String cronExpression, String project) {
        Result result = createScheduleTask(url, method, cronExpression, project);
        if (result.isSuccess()) {
            saveScheduleJob(url,method,cronExpression,project);
        }

        return result;
    }

    public Result createScheduleTask(String url, String method, String cronExpression, String project) {
        String jobName = createJobName(url,project,cronExpression);
        try {
            ScheduleUtil.createSchedule(this.getClass(),cronExpression,jobName,"executeMethod",url,project,method);
        } catch (ObjectAlreadyExistsException oaee) {
            logger.error(oaee.getMessage());
            return Result.fail("该定时任务已创建");
        } catch (SchedulerException se) {
            logger.error(se.getMessage());
            return Result.fail("该cron表达式指定时间已过期");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Result.fail("定时任务创建异常");
        }
        return Result.success(jobName);
    }

    public Result updateSchedule(String jobName, String project, String newCronExpression, String url, String method) {
        //停止并移除核心调度器中的任务
        try {
            ScheduleUtil.removeSchedule(jobName,project);
        } catch (Exception e) {
            return Result.fail("定时任务移除失败");
        }

        //更新任务信息
        Result result = createScheduleTask(url, method, newCronExpression, project);
        if (result.isSuccess()) {
            String newJobName = ScheduleUtil.createJobName(url,project,newCronExpression);
            scheduleService.updateJobCron(jobName, newCronExpression, newJobName);
        }

        return result;
    }

    public Result removeSchedule(String jobName, String project) {
        try {
            ScheduleUtil.removeSchedule(jobName,project);
            scheduleService.removeJob(jobName);
        } catch (Exception e) {
            return Result.fail("定时任务移除失败");
        }
        return Result.success();
    }

    public Result fireSchedule(String jobName, String project) {
        try {
            ScheduleUtil.removeSchedule(jobName,project);
            scheduleService.fireJob(jobName);
        } catch (Exception e) {
            return Result.fail("定时任务移除失败");
        }
        return Result.success();
    }

    public void executeMethod(String url, String method, String project, String cronExpression) {
        String jobName = createJobName(url,project,cronExpression);
        ScheduleLog log = new ScheduleLog();
        log.setJobName(jobName);
        log.setExecuteResult(ScheduleLog.RESULT_SUCCESS);
        log.setFailTimes(0);

        try{
            switch (method) {
                case HttpUtil.METHOD_GET:
                    log.setReturnContent(HttpUtil.get(url));break;
                case HttpUtil.METHOD_POST:
                    log.setReturnContent(HttpUtil.post(url));break;
                case HttpUtil.METHOD_PUT:
                    log.setReturnContent(HttpUtil.put(url));break;
                case HttpUtil.METHOD_DELETE:
                    log.setReturnContent(HttpUtil.delete(url));break;
                default:log.setReturnContent("default");break;
            }
        } catch (Exception e) {
            ScheduleLog origin = scheduleService.selectLatestOneByJobName(jobName);
            int failTimes = 1;
            if (origin != null) {
                failTimes = origin.getFailTimes() + 1;
            }
            log.setException(e.getMessage());
            log.setExecuteResult(ScheduleLog.RESULT_FAIL);
            log.setReturnContent(null);
            log.setFailTimes(failTimes);
            //失败超过最大限制次数或任务声明周期结束即删除任务
            if (failTimes >= ScheduleLog.DEFAULT_MAX_FAIL_TIMES || checkCronExpire(cronExpression)) {
                fireSchedule(jobName,project);
            }
        }

        scheduleService.saveScheduleLog(log);
    }

    private boolean checkCronExpire(String cron) {
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date after = cronExpression.getTimeAfter(new Date());
            if (after == null) {
                return true;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return true;
        }

        return false;
    }

    private void saveScheduleJob(String url, String method, String cronExpression, String project) {
        ScheduleJob job = new ScheduleJob();
        job.setRequestMethod(method);
        job.setJobName(createJobName(url,project,cronExpression));
        job.setCronExpression(cronExpression);
        job.setProject(project);
        job.setUrl(url);
        job.setStatus(ScheduleJob.STATUS_CREATE);

        scheduleService.saveScheduleJob(job);
    }
}
