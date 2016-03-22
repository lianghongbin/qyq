package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.HessianSerializer;
import com.jingoal.qyq.common.Message;
import com.jingoal.qyq.common.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by lianghb on 16/3/18.
 * Description: redis 消息队列消息分发器
 */
public class RedisMessageDispatcher {

    @Resource
    private JedisSentinelPool jedisSentinelPool;
    private static final Logger logger = LoggerFactory.getLogger(DefaultRedisProducer.class);
    private Map<String, RedisMessageListener<String>> listeners = new HashMap<String, RedisMessageListener<String>>();
    private Executor executor = Executors.newCachedThreadPool();    //默认线程池
    private Serializer serializer = new HessianSerializer(); //默认序列化类
    private volatile boolean isRunning = true;

    private RedisMessageDispatcher() {}

    private static class DispatcherHolder {
        private static RedisMessageDispatcher instance;
    }

    public static RedisMessageDispatcher getInstance() {
        return DispatcherHolder.instance;
    }

    public void addListener(RedisMessageListener<String> listener) {
        this.listeners.put(listener.key(), listener);
    }

    public void addListeners(List<RedisMessageListener<String>> listenerList) {
        for (RedisMessageListener<String> listener : listenerList)
            this.addListener(listener);
    }

    public void setListeners(List<RedisMessageListener<String>> listenerList) {
        this.listeners.clear();
        this.addListeners(listenerList);
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @SuppressWarnings("unchecked")
    public void start() {
        logger.info("redis message dispatcher starting......");

        for (final Map.Entry<String, RedisMessageListener<String>> entry : listeners.entrySet()) {
            executor.execute(new Runnable() {
                public void run() {
                    Jedis jedis = null;
                    try {
                        jedis = jedisSentinelPool.getResource();
                        logger.info("listening queue destination {} ......", entry.getKey());
                        while (isRunning) {
                            byte[] key = serializer.serialize(entry.getKey());
                            byte[] value = jedis.rpop(key);
                            if (value != null) {
                                entry.getValue().onMessage( (Message) serializer.deserialize(value));
                            }
                        }
                    } catch (Exception ex) {
                        jedisSentinelPool.returnBrokenResource(jedis);
                        logger.error("error[key=" + entry.getKey() + "]" + ex.getMessage(), ex);
                    } finally {
                        jedisSentinelPool.returnResource(jedis);
                    }
                }
            });
        }
    }

    public void shutdown() {
        this.isRunning = false;
    }
}
