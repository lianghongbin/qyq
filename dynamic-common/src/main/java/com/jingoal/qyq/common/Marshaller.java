package com.jingoal.qyq.common;

import java.rmi.MarshalException;
import java.rmi.UnmarshalException;

/**
 * Created by lianghb on 16/3/16.
 * Description: 序列化接口
 */
public interface Marshaller<T extends Keyable> {

    /**
     * 序列化/存储/缓存 实体
     * @param t 实体对象
     * @throws MarshalException 序列化异常
     */
    void marshal(T t) throws MarshalException;

    /**
     * 反序列化/获取 存储/缓存 实体
     * @param t key对象
     * @throws UnmarshalException 反序列化异常
     */
    T unmarshal(T t) throws UnmarshalException;
}
