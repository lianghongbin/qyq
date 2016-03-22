package com.jingoal.qyq.common.exception;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public class BasicException extends Exception {

    public BasicException() {
        super();
    }

    public BasicException(String msg) {
        super(msg);
    }

    public BasicException(Throwable e) {
        super(e);
    }

    public BasicException(String msg, Throwable e) {
        super(msg, e);
    }
}
