package com.jingoal.qyq.common;

import java.util.Collection;

/**
 * Created by lianghb on 16/3/16.
 * Description: 用户权重因数计算接口
 */
public interface WeightingFactor {

    /**
     * 根据条件,计算并生成用户权重列表
     * @return 用户列表
     */
    Collection<User> calculate();
}
