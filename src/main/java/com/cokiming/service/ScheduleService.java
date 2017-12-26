package com.cokiming.service;

import com.alibaba.fastjson.JSONObject;
import com.cokiming.common.annotation.LogInfo;
import com.cokiming.common.pojo.Result;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.ScheduleJobDao;
import com.cokiming.dao.ScheduleLogDao;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.dao.entity.ScheduleLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
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

    public String saveScheduleJob(ScheduleJob scheduleJob) {
        String id = scheduleJobDao.saveJob(scheduleJob);
        logger.info("保存了job：" + scheduleJob);
        return id;
    }

    public void deleteById(String id) {
        scheduleJobDao.deleteById(new ObjectId(id));
    }

    public ScheduleLog selectLatestOneByJobId(String jobId) {
        return scheduleLogDao.selectLatestByJobId(jobId);
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

        //holder中写死的任务
        Class<ScheduleHolder> holderClass = ScheduleHolder.class;
        Method[] methods = holderClass.getDeclaredMethods();
        for (Method method : methods) {
            LogInfo logInfo = method.getAnnotation(LogInfo.class);
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (logInfo != null) {
                ScheduleJob job = new ScheduleJob();
                job.setId(logInfo.id());
                job.setName(logInfo.name());
                job.setUrl(logInfo.url());
                job.setProject(logInfo.project());
                job.setCronExpression(scheduled.cron());
                job.setDescription(logInfo.description());
                jobList.add(job);
            }
        }

        return jobList;
    }

    public List<ScheduleJob> selectFiredJobs() {
        //数据库已存的任务
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_FIRED);
        List<ScheduleJob> jobList = selectByModel(model);

        return jobList;
    }

    public Result selectLogsByPage(String jobId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        int limit = pageSize;
        long num = scheduleLogDao.countByJobId(jobId);
        List<ScheduleLog> scheduleLogs = scheduleLogDao.selectByPage(jobId, offset, limit);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum",(num - 1)/pageSize + 1);
        jsonObject.put("logList",scheduleLogs);

        return Result.success(jsonObject);
    }

    public void updateJobCron(String id,String cron,String newJobName,String description,String url,String status) {
        ScheduleJob condition = new ScheduleJob();
        condition.setId(id);
        condition.setStatus(status);

        ScheduleJob model = new ScheduleJob();
        model.setCronExpression(cron);
        model.setJobName(newJobName);
        model.setDescription(description);
        model.setUrl(url);
        model.setStatus(ScheduleJob.STATUS_CREATE);

        scheduleJobDao.updateByCondition(condition,model);
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
