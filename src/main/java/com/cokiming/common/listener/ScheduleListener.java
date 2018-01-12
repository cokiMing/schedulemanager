package com.cokiming.common.listener;

import com.cokiming.common.annotation.LogInfo;
import com.cokiming.common.exception.DuplicateJobIdException;
import com.cokiming.dao.entity.ScheduleJob;
import com.cokiming.service.ScheduleHolder;
import com.cokiming.service.ScheduleManager;
import com.cokiming.service.ScheduleService;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/15.
 */
@Component
public class ScheduleListener implements ApplicationListener<ApplicationContextEvent> {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ScheduleManager scheduleManager;

    private Log logger = LogFactory.getLog(ScheduleListener.class);

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            startJob();
        } else if (event instanceof ContextClosedEvent) {
            stopJob();
        }
    }

    private void startJob() {
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.Status.CREATE);
        model.setStarted(ScheduleJob.Started.OFF);
        List<ScheduleJob> scheduleJobs = scheduleService.selectByModel(model);
        Set<String> idSet = Sets.newHashSet();

        for (ScheduleJob scheduleJob : scheduleJobs) {
            try {
                scheduleManager.startSchedule(scheduleJob);
                logger.info("启动任务: " + scheduleJob.getName());
                idSet.add(scheduleJob.getId());
            } catch (SchedulerException e) {
                //启动失败的过期任务直接清除
                logger.error("启动失败: " + e.getMessage());
                scheduleManager.fireSchedule(scheduleJob.getJobName(),scheduleJob.getProject());
            } catch (Exception e) {
                logger.error("启动失败: " + e.getMessage());
            }
        }

        //校验写死的任务中id是否有重复
        Class<ScheduleHolder> scheduleHolderClass = ScheduleHolder.class;
        Method[] methods = scheduleHolderClass.getDeclaredMethods();
        for (Method method : methods) {
            LogInfo logInfo = method.getAnnotation(LogInfo.class);
            if (logInfo != null) {
                if (idSet.contains(logInfo.id())) {
                    throw new DuplicateJobIdException("duplicate jobId: " + logInfo.id());
                } else {
                    idSet.add(logInfo.id());
                }
            }
        }

        logger.info("启动备份定时任务完成");
    }

    private void stopJob() {
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.Status.CREATE);
        model.setStarted(ScheduleJob.Started.ON);
        List<ScheduleJob> scheduleJobs = scheduleService.selectByModel(model);

        for (ScheduleJob scheduleJob : scheduleJobs) {
            try {
                scheduleManager.stopSchedule(scheduleJob);
                logger.info("停止任务: " + scheduleJob.getName());
            } catch (Exception e) {
                logger.error("停止失败: " + e.getMessage());
            }
        }

        logger.info("schedule任务停止完成");
    }
}
