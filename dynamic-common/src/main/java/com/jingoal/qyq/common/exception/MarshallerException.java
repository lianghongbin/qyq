package com.jingoal.qyq.common.exception;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public class MarshallerException extends BasicException {

    public MarshallerException() {
        super();
    }

    public MarshallerException(String msg) {
        super(msg);
    }

    public MarshallerException(Throwable e) {
        super(e);
    }

    public MarshallerException(String msg, Throwable e) {
        super(msg, e);
    }
}
