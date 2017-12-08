package com.cokiming.service;

import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Service
public class ScheduleService {

    private Log logger = LogFactory.getLog(this.getClass());

    public void saveScheduleLog(ScheduleLog scheduleLog) {
        scheduleLog.setExecuteTime(new Date());
        logger.info("保存了log：" + scheduleLog);
    }

    public void saveScheduleJob(ScheduleJob scheduleJob) {
        Date current = new Date();
        scheduleJob.setUpdateTime(current);
        scheduleJob.setCreateTime(current);
        logger.info("保存了job：" + scheduleJob);
    }

    public ScheduleLog selectOneByModel(ScheduleLog scheduleLog) {
        return new ScheduleLog();
    }

    public ScheduleJob selectOneByModel(ScheduleJob scheduleJob) {
        return new ScheduleJob();
    }

    public void removeJob(String jobName) {
        logger.info("移除了："+ jobName);
    }
}
