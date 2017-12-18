package com.cokiming.common.listener;

import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.service.ScheduleManager;
import com.cokiming.service.ScheduleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/15.
 */
@Component
public class ScheduleListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleManager scheduleManager;

    private Log logger = LogFactory.getLog(ScheduleListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.STATUS_CREATE);
        List<ScheduleJob> scheduleJobs = scheduleService.selectByModel(model);

        for (ScheduleJob scheduleJob : scheduleJobs) {
            try {
                scheduleManager.startSchedule(scheduleJob);
                logger.info("启动任务: " + scheduleJob.getJobName());
            } catch (SchedulerException e) {
                //启动失败的过期任务直接清除
                scheduleManager.fireSchedule(scheduleJob.getJobName(),scheduleJob.getProject());
                logger.error("启动失败: " + e.getMessage());
            } catch (Exception e) {
                logger.error("启动失败: " + e.getMessage());
            }
        }

        logger.info("启动备份定时任务完成");
    }
}