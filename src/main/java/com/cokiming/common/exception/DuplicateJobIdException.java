package com.cokiming.common.exception;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/25.
 */
public class DuplicateJobIdException extends RuntimeException {

    public DuplicateJobIdException(String message) {
        super(message);
    }
}
