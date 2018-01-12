package com.cokiming.service;

import com.cokiming.common.http.HttpUtil;
import com.cokiming.dao.entity.ScheduleJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 在这里添加的定时任务无法通过接口更改，
 * 请增加@LogInfo注解，其中id不能重复，否则项目无法正常启动
 *
 * @author wuyiming
 * Created by wuyiming on 2017/12/18.
 */
@Service
public class ScheduleHolder {

    private Log logger = LogFactory.getLog(this.getClass());

    @Value("${cokiming.heartbeat.host}")
    private String TWIN_HOST;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ScheduleManager scheduleManager;

    private final static int DEAD = 0;
    private final static int ALIVE = 1;
    private final static int DUMP = 2;

    private static int isTwinAlive = DEAD;

    /**
     * 心跳包
     * @return
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void heartBeat() {
        try {
            HttpUtil.get(TWIN_HOST + "/scheduleManager/heartbeat");
            successTwin();
        } catch (Exception e) {
            logger.error(e.getMessage());
            failTwin();
            if (isTwinAlive == DUMP) {
                logger.info("开始重启任务...");
                stopSchedules();
                startSchedules();
                logger.info("重启完成...");
            } else {
                logger.info("无联机实例");
            }
        }
    }

    private void stopSchedules() {
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.Status.CREATE);
        List<ScheduleJob> scheduleJobs = scheduleService.selectByModel(model);
        for (ScheduleJob job : scheduleJobs) {
            try {
                scheduleManager.stopSchedule(job);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    private void startSchedules() {
        ScheduleJob model = new ScheduleJob();
        model.setStatus(ScheduleJob.Status.CREATE);
        model.setStarted(ScheduleJob.Started.OFF);
        List<ScheduleJob> scheduleJobs = scheduleService.selectByModel(model);
        for (ScheduleJob job : scheduleJobs) {
            try {
                scheduleManager.startSchedule(job);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    private void successTwin() {
        switch (isTwinAlive) {
            default:
                isTwinAlive = ALIVE;break;
        }
    }

    private void failTwin() {
        switch (isTwinAlive) {
            case ALIVE:
                isTwinAlive = DUMP;break;
            default:
                isTwinAlive = DEAD;break;
        }
    }
}
