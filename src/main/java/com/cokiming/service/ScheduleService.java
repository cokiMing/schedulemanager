package com.cokiming.service;

import com.cokiming.common.annotation.LogInfo;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.ScheduleJobDao;
import com.cokiming.dao.ScheduleLogDao;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
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

    public ScheduleJob selectJobById(String id) {
        return scheduleJobDao.selectById(id);
    }

    public List<ScheduleJob> selectByModel(ScheduleJob scheduleJob) {
        return scheduleJobDao.selectByModel(scheduleJob);
    }

    public List<ScheduleJob> selectAvailableJobs() {
        //数据库已存的任务
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_CREATE);
        List<ScheduleJob> jobList = selectByModel(model);

        //manager中写死的任务
        Class<ScheduleManager> managerClass = ScheduleManager.class;
        Method[] methods = managerClass.getDeclaredMethods();
        for (Method method : methods) {
            LogInfo logInfo = method.getAnnotation(LogInfo.class);
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (logInfo != null) {
                ScheduleJob job = new ScheduleJob();
                job.setUrl(logInfo.url());
                job.setProject(logInfo.project());
                job.setCronExpression(scheduled.cron());
                job.setJobName(ScheduleUtil.createJobName(logInfo.url(),logInfo.project(),scheduled.cron()));
                jobList.add(job);
            }
        }

        return jobList;
    }

    public List<ScheduleLog> selectLogsByPage(String jobName,int pageNo,int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        int limit = pageSize;
        return scheduleLogDao.selectByPage(jobName,offset,limit);
    }

    public ScheduleLog selectOneByModel(ScheduleLog scheduleLog) {
        return new ScheduleLog();
    }

    public ScheduleJob selectOneByModel(ScheduleJob scheduleJob) {
        return new ScheduleJob();
    }

    public ScheduleJob getDeathJob(String jobName) {
        return scheduleJobDao.selectDeathJob(jobName);
    }

    public void resumeSchedule(String jobName) {
        ScheduleJob condition = new ScheduleJob();
        condition.setJobName(jobName);

        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_CREATE);

        scheduleJobDao.updateByCondition(condition,model);
        logger.info("恢复了："+ jobName);
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
