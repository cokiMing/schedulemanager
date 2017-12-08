package com.cokiming.service;

import com.cokiming.common.annotation.LogInfo;
import com.cokiming.common.http.HttpUtil;
import com.cokiming.common.pojo.Result;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Scheduled(cron = "0 0/5 * * * ?")
    @LogInfo(url = "www.baidu.com",description = "test schedule",project = "schedule")
    public String test() {
        logger.info("test...");
        String result = "ok！";
        return result;
    }

    public Result createSchedule(String url, String method, Date targetTime, String project) {
        String cron = ScheduleUtil.convertDateToCron(targetTime);
        return createSchedule(url,method,cron,project);
    }

    public Result createSchedule(String url, String method, String cronExpression, String project) {
        String jobName = createJobName(url,project,cronExpression);
        try {
            ScheduleUtil.createSchedule(this.getClass(),cronExpression,jobName,"executeMethod",url,project,method);
            saveScheduleJob(url,method,cronExpression,project);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("定时任务创建异常");
        }
        return Result.success();
    }

    public Result updateSchedule(String url, String originCronExpression, String project, String newCronExpression) {
        Result result = removeSchedule(url, originCronExpression, project);
        if (!result.isSuccess()) {
            return result;
        }

        ScheduleJob job = scheduleService.selectOneByModel(new ScheduleJob());
        return createSchedule(url,job.getRequestMethod(),newCronExpression,project);
    }

    public Result removeSchedule(String url, String cronExpression, String project) {
        String jobName = createJobName(url,project,cronExpression);
        try {
            ScheduleUtil.removeSchedule(jobName,project);
            scheduleService.removeJob(jobName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("定时任务移除失败");
        }
        return Result.success();
    }

    public void executeMethod(String url, String method, String project, String cronExpression) {
        ScheduleLog log = new ScheduleLog();
        log.setJobName(createJobName(url,project,cronExpression));
        log.setExecuteResult(ScheduleLog.RESULT_SUCCESS);

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
            log.setException(e.getMessage());
            log.setExecuteResult(ScheduleLog.RESULT_FAIL);
            log.setReturnContent(null);
        }

        logger.info(log);
        scheduleService.saveScheduleLog(log);
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
