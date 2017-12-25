package com.cokiming.service;

import com.cokiming.common.annotation.LogInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 在这里添加的定时任务无法通过接口更改，
 * 请增加@LogInfo注解，且id不能重复，否则项目无法正常启动
 *
 * @author wuyiming
 * Created by wuyiming on 2017/12/18.
 */
@Service
public class ScheduleHolder {

    private Log logger = LogFactory.getLog(this.getClass());

    /** example
    @Scheduled(cron = "0 0/10 * * * ?")
    @LogInfo(id = "schedule001", name = "test", url = "www.baidu.com",description = "test schedule",project = "schedule")
    public String test() {
        logger.info("test...");
        String result = "ok！";
        return result;
    }
    */

}
