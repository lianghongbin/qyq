package com.jingoal.qyq.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lianghb on 16/3/16.
 * Description: 消息体
 */
public class Message<T extends Serializable> implements Serializable{

    private static final long serialVersionID = 1L;
    private Map<String, Object> params = new HashMap<String, Object>();
    private final T payload;

    public Message(T t) {
        this.payload = t;
    }

    public static long getSerialVersionID() {
        return serialVersionID;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public T getPayload() {
        return payload;
    }
}
