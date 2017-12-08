package com.cokiming.common.aspect;

import com.cokiming.common.annotation.LogInfo;
import com.cokiming.common.util.ScheduleUtil;
import com.cokiming.dao.entity.ScheduleLog;
import com.cokiming.service.ScheduleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wuyiming
 *         Created by wuyiming on 2017/12/7.
 */
@Component
@Aspect
public class ScheduleLogAspect {

    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ScheduleService scheduleService;

    @Pointcut("@annotation(com.cokiming.common.annotation.LogInfo)")
    public void logInfoPointCut() {

    }

    @Around("logInfoPointCut()")
    public String doAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        String returnContent = (String) joinPoint.proceed();
        if (returnContent != null) {
            ScheduleLog log = createLog(joinPoint, null);
            log.setReturnContent(returnContent);
            scheduleService.saveScheduleLog(log);
        }
        return returnContent;
    }

    @AfterThrowing(throwing = "e",value = "logInfoPointCut()")
    private void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        ScheduleLog log = createLog(joinPoint, e);
        scheduleService.saveScheduleLog(log);
    }

    private ScheduleLog createLog(JoinPoint joinPoint, Throwable e) {
        //拦截的实体类
        Object target = joinPoint.getTarget();
        //拦截的方法名称
        String methodName = joinPoint.getSignature().getName();
        //拦截的放参数类型
        Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

        ScheduleLog log = new ScheduleLog();
        try{
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            LogInfo logInfo = method.getAnnotation(LogInfo.class);
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            log.setMethodName(methodName);
            log.setExecuteResult(null);
            log.setJobName(ScheduleUtil.createJobName(logInfo.url(),logInfo.project(),scheduled.cron()));
            if (e != null) {
                log.setException(e.getMessage());
                log.setExecuteResult(ScheduleLog.RESULT_FAIL);
            } else {
                log.setExecuteResult(ScheduleLog.RESULT_SUCCESS);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        logger.info(log);
        return log;
    }
}
