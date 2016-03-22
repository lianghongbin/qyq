package com.jingoal.qyq.common;

/**
 * Created by lianghb on 16/3/16.
 * Description: 活跃用户接口
 */
public interface Activity {

    /**
     * 判断用户是否是活跃用户
     * @param user 用户实体
     * @return true/false
     */
    boolean isActive(User user);
}
