package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.Serializer;
import com.jingoal.qyq.common.exception.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/17.
 * Description:
 */
public class DefaultRedisProducer<T extends Serializable> implements RedisProducer<T> {

    private JedisSentinelPool jedisSentinelPool;
    private Serializer serializer;
    private static final Logger logger = LoggerFactory.getLogger(DefaultRedisProducer.class);

    public void setJedisSentinelPool(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public void send(RedisMessage<T> message) throws MessageException {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            byte[] key = serializer.serialize(message.key());
            byte[] value = serializer.serialize(message);
            jedis.lpush(key, value);
            jedis.expire(key, message.expire());
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + message.key() + " seconds=" + message.expire()
                    + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }
}
