package com.jingoal.qyq.common;

/**
 * Created by lianghb on 16/3/21.
 * Description: 序列化接口
 */
public interface Serializer {

    /**
     * 序列化接口
     * @param obj 序列化对象
     * @return 字节流
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化接口
     * @param bytes 字节流
     * @return 对象
     */
    Object deserialize(byte[] bytes);
}
