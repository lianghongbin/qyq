package com.jingoal.qyq.common;

/**
 * Created by lianghb on 16/3/16.
 * Description: 企业用户实体
 */
public class User implements Keyable {

    private long id;
    private String name;
    private int factor; //权重因子

    public String key() {
        return null;
    }
}
