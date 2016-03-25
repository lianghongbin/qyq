package com.jingoal.qyq.web.service;

import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.common.Row;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lianghb on 16/3/18.
 * Description: feed流数据操作接口
 */
public interface FeedService {

    /**
     * 发布一个feed 动态
     *
     * @param feed 动态
     */
    void add(Feed feed);

    /**
     * 个人删除某个动态
     *
     * @param feed 动态
     */
    void remove(Feed feed);

    /**
     * 个人删除某个动态,连同关注人的feed流中一并删除
     *
     * @param feed 要删除的动态
     * @param cIds 个人关注的企业ID列表
     */
    void removeAndInbox(Feed feed, List<Long> cIds);

    /**
     * 取消关注某人时,将该用户feed流中的动态全部删除
     * @param cId 取消关注的人的ID
     * @param feeds 需要删除的动态列表
     */
    void removeFromInbox(long cId, List<Feed> feeds);

    /**
     * feeds 批量合并到用户 userId的feed流中
     *
     * @param userId 用户ID
     * @param feeds  动态列表
     */
    void merge(long userId, Set<Feed> feeds);

    /**
     * 批量合并
     *
     * @param data 合并数据
     */
    void merge(Map<Long, Feed> data);

    /**
     * feeds 合并到用户 userId的feed流中
     *
     * @param userId 用户ID
     * @param feed   动态列表
     */
    void merge(long userId, Feed feed);

    /**
     * 根据用户 userId, 获取该用户发的所有动态列表
     *
     * @param userId    用户ID
     * @param timestamp 时间戳,如果从最新的获取, 此值为0
     * @param size      获取的条数
     * @return feed流ID列表
     */
    Set<Row> loadOutbox(long userId, long timestamp, int size);

    /**
     * 根据用户 userId列表, 批量获取该用户发的所有动态列表
     *
     * @param userIds   用户ID 列表
     * @param timestamp 时间戳,如果从最新的获取, 此值为0
     * @param size      每人获取的条数
     * @return feed流ID列表
     */
    Set<Row> batchLoadOutbox(List<Long> userIds, long timestamp, int size);

    /**
     * 根据用户 userId, 获取该用户的feed流
     *
     * @param userId    用户ID
     * @param timestamp 时间戳
     * @param size      获取的条数
     * @return feed流ID列表
     */
    Set<Row> loadInbox(long userId, long timestamp, int size);
}
