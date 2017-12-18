package com.cokiming.service;

import com.cokiming.common.annotation.LogInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/18.
 */
@Service
public class ScheduleHolder {

    private Log logger = LogFactory.getLog(this.getClass());

    @Scheduled(cron = "0 0/2 * * * ?")
    @LogInfo(url = "www.baidu.com",description = "test schedule",project = "schedule")
    public String test() {
        logger.info("test...");
        String result = "ok！";
        return result;
    }
}
