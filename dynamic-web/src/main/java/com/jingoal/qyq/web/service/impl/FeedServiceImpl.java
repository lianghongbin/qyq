package com.jingoal.qyq.web.service.impl;

import com.jingoal.qyq.common.Constants;
import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.web.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
@Service
public class FeedServiceImpl implements FeedService {

    @Resource
    private JedisSentinelPool jedisSentinelPool;
    private static final Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

    /**
     * feeds 合并到用户 userId的feed流中
     *
     * @param userId 用户ID
     * @param feeds  动态列表
     */
    public void merge(long userId, Set<Feed> feeds) {
        Jedis jedis = null;
        Map<String, Double> map = new HashMap<String, Double>(feeds.size());
        for (Feed feed : feeds) {
            map.put(Long.toString(feed.getId()), (double) feed.getTime());
        }
        try {
            jedis = jedisSentinelPool.getResource();
            jedis.zadd(Constants.FEED_INBOX_KEY_PREFIX + userId, map);
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + userId + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

    /**
     * feeds 合并到用户 userId的feed流中
     *
     * @param userId 用户ID
     * @param feed   动态列表
     */
    public void merge(long userId, Feed feed) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            jedis.zadd(Constants.FEED_INBOX_KEY_PREFIX + userId, (double)feed.getTime(), Long.toString(feed.getId()));
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + userId + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

    /**
     * 根据用户 userId, 获取该用户发的所有动态列表
     *
     * @param userId 用户ID
     * @return 用户动态列表
     */
    public List<Long> loadOutbox(long userId) {
        return null;
    }

    /**
     * 根据用户 userId, 获取该用户的feed流
     *
     * @param userId    用户ID
     * @param timestamp 时间戳
     * @param size      获取的条数
     * @return feed流ID列表
     */
    public List<Long> loadInbox(long userId, long timestamp, int size) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Set<String> feeds;
            if (timestamp <=0) {
                feeds = jedis.zrevrange(Constants.FEED_INBOX_KEY_PREFIX + userId, 0, size);
            }
            else {
                long min = 61407043200000L; //2015-11-1
                feeds = jedis.zrevrangeByScore(Constants.FEED_INBOX_KEY_PREFIX + userId, timestamp, min, 0, size);
            }

            List<Long> ids = new ArrayList<Long>(feeds.size());
            for (String f : feeds) {
                ids.add(Long.parseLong(f));
            }

            return ids;
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + userId + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return new ArrayList<Long>(0);
    }
}