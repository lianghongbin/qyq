package com.jingoal.qyq.common;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public interface Keyable extends Serializable {

    /**
     * 生成缓存Key
     * @return string
     */
    String key();
}
