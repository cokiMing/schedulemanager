package com.cokiming.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.cokiming.common.framework.HttpContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/1/2.
 */
@Component
@Aspect
public class ControllerAspect {

    private Log logger = LogFactory.getLog(this.getClass());

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingPointCut() {

    }

    @AfterReturning(value = "requestMappingPointCut()", returning = "retVal")
    public void doLogController(JoinPoint joinpoint, Object retVal) {
        String code = ((JSONObject) retVal).getString("code");
        HttpServletResponse response = HttpContextHolder.getResponse();
        switch (code) {
            case "1": response.setStatus(200);break;
            case "0": response.setStatus(400);break;
            case "-1": response.setStatus(500);break;
            default: response.setStatus(200);break;
        }
    }

}
