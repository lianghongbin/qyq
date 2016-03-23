package com.jingoal.qyq.web.service.impl;

import com.jingoal.qyq.common.Constants;
import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.web.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private JedisPool jedisSentinelPool;
    private static final Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

    /**
     * 发布一个feed 动态
     *
     * @param feed 动态
     */
    public void add(Feed feed) {
        Jedis jedis = null;

        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            //把我发的动态存入我的outbox
            pl.zadd(Constants.FEED_OUTBOX_KEY_PREFIX + feed.getCid(), (double)feed.getTime(), Long.toString(feed.getId()));
            //同时,把我发的动态存入我发的inbox里面,用以回显
            pl.zadd(Constants.FEED_INBOX_KEY_PREFIX + feed.getCid(), (double)feed.getTime(), Long.toString(feed.getId()));
            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + feed.getCid() + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

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
    public List<Long> loadOutbox(long userId, long timestamp, int size) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Set<String> feeds;
            if (timestamp <=0) {
                feeds = jedis.zrevrange(Constants.FEED_OUTBOX_KEY_PREFIX + userId, 0, size);
            }
            else {
                long min = 61407043200000L; //2015-11-1
                feeds = jedis.zrevrangeByScore(Constants.FEED_OUTBOX_KEY_PREFIX + userId, timestamp, min, 0, size);
            }

            List<Long> ids = new ArrayList<Long>(feeds.size());
            for (String f : feeds) {
                ids.add(Long.parseLong(f));
            }

            return ids;
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_OUTBOX_KEY_PREFIX + userId + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return new ArrayList<Long>(0);
    }

    /**
     * 根据用户 userId列表, 批量获取该用户发的所有动态列表
     *
     * @param userIds    用户ID 列表
     * @param timestamp 时间戳,如果从最新的获取, 此值为0
     * @param size      每人获取的条数
     * @return feed流ID列表
     */
    public List<Long> batchLoadOutbox(List<Long> userIds, long timestamp, int size) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            List feeds;
            for (long userId : userIds) {
                if (timestamp <=0) {
                    pl.zrevrange(Constants.FEED_OUTBOX_KEY_PREFIX + userId, 0, size);
                }
                else {
                    long min = 61407043200000L; //2015-11-1
                    pl.zrevrangeByScore(Constants.FEED_OUTBOX_KEY_PREFIX + userId, timestamp, min, 0, size);
                }
            }

            feeds = pl.syncAndReturnAll();

            List<Long> ids = new ArrayList<Long>(feeds.size());
            for (Object f : feeds) {
                ids.addAll((Set) f);
            }

            return ids;
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[batch pipeline fetch feeds]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return new ArrayList<Long>(0);
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
