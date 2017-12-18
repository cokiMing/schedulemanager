package com.cokiming.service;

import com.cokiming.dao.ScheduleJobDao;
import com.cokiming.dao.ScheduleLogDao;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Service
public class ScheduleService {

    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleLogDao scheduleLogDao;

    public void saveScheduleLog(ScheduleLog scheduleLog) {
        scheduleLogDao.saveLog(scheduleLog);
        logger.info("保存了log：" + scheduleLog);
    }

    public void saveScheduleJob(ScheduleJob scheduleJob) {
        scheduleJobDao.saveJob(scheduleJob);
        logger.info("保存了job：" + scheduleJob);
    }

    public ScheduleLog selectLatestOneByJobName(String jobName) {
        return scheduleLogDao.selectLatestByJobName(jobName);
    }

    public List<ScheduleJob> selectByModel(ScheduleJob scheduleJob) {
        return scheduleJobDao.selectByModel(scheduleJob);
    }

    public ScheduleLog selectOneByModel(ScheduleLog scheduleLog) {
        return new ScheduleLog();
    }

    public ScheduleJob selectOneByModel(ScheduleJob scheduleJob) {
        return new ScheduleJob();
    }

    public void removeJob(String jobName) {
        ScheduleJob condition = new ScheduleJob();
        condition.setJobName(jobName);
        condition.setStatus(ScheduleJob.STATUS_CREATE);

        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_DELETE);

        scheduleJobDao.updateByCondition(condition,model);
        logger.info("移除了："+ jobName);
    }

    public void fireJob(String jobName) {
        ScheduleJob condition = new ScheduleJob();
        condition.setJobName(jobName);
        condition.setStatus(ScheduleJob.STATUS_CREATE);

        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_FIRED);

        scheduleJobDao.updateByCondition(condition,model);
        logger.info("系统删除了："+ jobName);
    }
}
