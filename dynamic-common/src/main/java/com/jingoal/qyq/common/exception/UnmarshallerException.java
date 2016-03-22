package com.jingoal.qyq.common.exception;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public class UnmarshallerException extends BasicException {
    public UnmarshallerException() {
        super();
    }

    public UnmarshallerException(String msg) {
        super(msg);
    }

    public UnmarshallerException(Throwable e) {
        super(e);
    }

    public UnmarshallerException(String msg, Throwable e) {
        super(msg, e);
    }
}
