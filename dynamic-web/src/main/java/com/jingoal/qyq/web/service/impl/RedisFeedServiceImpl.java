package com.jingoal.qyq.web.service.impl;

import com.jingoal.qyq.common.Constants;
import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.common.Row;
import com.jingoal.qyq.web.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
@Service
public class RedisFeedServiceImpl implements FeedService {

    @Autowired
    private JedisPool jedisSentinelPool;
    private static final Logger logger = LoggerFactory.getLogger(RedisFeedServiceImpl.class);

    /**
     * 发布一个feed 动态
     * 发布到我的outbox同时,在我自己的inbox里面插入这条动态
     *
     * @param feed 动态
     */
    public void add(Feed feed) {
        Jedis jedis = null;

        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            //把我发的动态存入我的outbox
            pl.zadd(Constants.FEED_OUTBOX_KEY_PREFIX + feed.getCid(), (double) feed.getTime(), Long.toString(feed.getId()));
            //同时,把我发的动态存入我发的inbox里面,用以回显
            pl.zadd(Constants.FEED_INBOX_KEY_PREFIX + feed.getCid(), (double) feed.getTime(), Long.toString(feed.getId()));
            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + feed.getCid() + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

    /**
     * 删除某个动态
     *
     * @param feed 动态
     */
    @Override
    public void remove(Feed feed) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            pl.zrem(Constants.FEED_OUTBOX_KEY_PREFIX + feed.getCid(), Long.toString(feed.getId()));
            pl.zrem(Constants.FEED_INBOX_KEY_PREFIX + feed.getCid(), Long.toString(feed.getId()));
            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_OUTBOX_KEY_PREFIX + feed.getCid() + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

    /**
     * 个人删除某个动态,连同关注人的feed流中一并删除
     *
     * @param feed 要删除的动态
     * @param cIds 个人关注的企业ID列表
     */
    @Override
    public void removeAndInbox(Feed feed, List<Long> cIds) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            pl.zrem(Constants.FEED_OUTBOX_KEY_PREFIX + feed.getCid(), Long.toString(feed.getId()));
            pl.zrem(Constants.FEED_INBOX_KEY_PREFIX + feed.getCid(), Long.toString(feed.getId()));

            for (Long cId : cIds) {
                pl.zrem(Constants.FEED_INBOX_KEY_PREFIX + cId, Long.toString(feed.getId()));
            }

            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error " + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }
    }

    /**
     * 取消关注某人时,将该用户feed流中的动态全部删除
     *
     * @param cId   取消关注的人的ID
     * @param feeds 需要删除的动态列表
     */
    @Override
    public void removeFromInbox(long cId, List<Feed> feeds) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            for (Feed feed : feeds) {
                pl.zrem(Constants.FEED_INBOX_KEY_PREFIX + cId, Long.toString(feed.getId()));
            }

            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + Constants.FEED_INBOX_KEY_PREFIX + cId + "]" + ex.getMessage(), ex);
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
        Map<String, Double> map = new HashMap<>(feeds.size());
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
     * 批量合并
     *
     * @param data 合并数据
     */
    @Override
    public void merge(Map<Long, Feed> data) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();
            for (Map.Entry<Long, Feed> entry : data.entrySet()) {
                pl.zadd(Constants.FEED_INBOX_KEY_PREFIX + entry.getKey(), (double) entry.getValue().getTime(), Long.toString(entry.getValue().getId()));
            }

            pl.sync();
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error merge error:" + ex.getMessage(), ex);
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
            jedis.zadd(Constants.FEED_INBOX_KEY_PREFIX + userId, (double) feed.getTime(), Long.toString(feed.getId()));
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
    public Set<Row> loadOutbox(long userId, long timestamp, int size) {

        return fetchRow(Constants.FEED_OUTBOX_KEY_PREFIX + userId, timestamp, size);
    }

    /**
     * 根据用户 userId列表, 批量获取该用户发的所有动态列表
     *
     * @param userIds   用户ID 列表
     * @param timestamp 时间戳,如果从最新的获取, 此值为0
     * @param size      每人获取的条数
     * @return feed流ID列表
     */
    @SuppressWarnings("unchecked")
    public Set<Row> batchLoadOutbox(List<Long> userIds, long timestamp, int size) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Pipeline pl = jedis.pipelined();

            List feeds;
            long min = 1446358543926L; //2015-10-1
            for (long userId : userIds) {
                if (timestamp <= 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 1);
                    long max = calendar.getTimeInMillis();

                    pl.zrevrangeByScoreWithScores(Constants.FEED_OUTBOX_KEY_PREFIX + userId, max, min, 0, size);
                } else {
                    pl.zrevrangeByScoreWithScores(Constants.FEED_OUTBOX_KEY_PREFIX + userId, timestamp, min, 0, size);
                }
            }

            feeds = pl.syncAndReturnAll();

            Set<Row> ids = new TreeSet<>();
            for (Object f : feeds) {
                Set<Tuple> set = (Set<Tuple>) f;

                for (Tuple tuple :set) {
                    ids.add(new Row(tuple.getElement(), tuple.getScore()));
                }
            }

            return ids;
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[batch pipeline fetch feeds]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return new TreeSet<>();
    }

    /**
     * 根据用户 userId, 获取该用户的feed流
     *
     * @param userId    用户ID
     * @param timestamp 时间戳
     * @param size      获取的条数
     * @return feed流ID列表
     */
    public Set<Row> loadInbox(long userId, long timestamp, int size) {
        return fetchRow(Constants.FEED_INBOX_KEY_PREFIX + userId, timestamp, size);
    }

    /**
     * 通用的获取列表方法
     * @param key 缓存Key
     * @param timestamp 最新截至时间戳
     * @param size 获取数量
     * @return 结果列表
     */
    private Set<Row> fetchRow(String key, long timestamp, int size) {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            Set<Tuple> feeds;
            long min = 1446358543926L; //2015-10-1

            if (timestamp <= 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR, 1);
                long max = calendar.getTimeInMillis();

                feeds = jedis.zrevrangeByScoreWithScores(key, max, min, 0, size);

            } else {
                feeds = jedis.zrevrangeByScoreWithScores(key, timestamp, min, 0, size);
            }

            Set<Row> ids = new TreeSet<>();
            for (Tuple f : feeds) {
                ids.add(new Row(f.getElement(), f.getScore()));
            }

            return ids;
        } catch (Exception ex) {
            jedisSentinelPool.returnBrokenResource(jedis);
            logger.error("error[key=" + key + "]" + ex.getMessage(), ex);
        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return new TreeSet<>();
    }
}
