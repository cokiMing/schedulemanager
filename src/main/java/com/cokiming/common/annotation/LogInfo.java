package com.cokiming.common.annotation;

import java.lang.annotation.*;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogInfo {

    String url();

    String description();

    String project();
}
