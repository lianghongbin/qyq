package com.jingoal.qyq.common.exception;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public class MessageException extends BasicException {
    public MessageException() {
        super();
    }

    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(Throwable e) {
        super(e);
    }

    public MessageException(String msg, Throwable e) {
        super(msg, e);
    }
}
