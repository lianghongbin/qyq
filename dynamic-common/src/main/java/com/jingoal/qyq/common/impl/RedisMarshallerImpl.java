package com.jingoal.qyq.common.impl;

import com.jingoal.qyq.common.Marshaller;
import com.jingoal.qyq.common.Cacheable;

import java.rmi.MarshalException;
import java.rmi.UnmarshalException;

/**
 * Created by lianghb on 16/3/16.
 * Description:
 */
public class RedisMarshallerImpl implements Marshaller<Cacheable> {

    public void marshal(Cacheable cacheable) throws MarshalException {

    }

    public Cacheable unmarshal(Cacheable cacheable) throws UnmarshalException {
        return null;
    }
}
