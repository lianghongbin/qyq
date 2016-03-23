package com.jingoal.qyq.web.service;

import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.common.Page;

import java.util.List;
import java.util.Set;

/**
 * Created by lianghb on 16/3/18.
 * Description: feed流数据操作接口
 */
public interface FeedService {

    /**
     * 发布一个feed 动态
     * @param feed 动态
     */
    void add(Feed feed);

    /**
     * feeds 批量合并到用户 userId的feed流中
     *
     * @param userId 用户ID
     * @param feeds  动态列表
     */
    void merge(long userId, Set<Feed> feeds);

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
    List<Long> loadOutbox(long userId, long timestamp, int size);

    /**
     * 根据用户 userId列表, 批量获取该用户发的所有动态列表
     *
     * @param userIds    用户ID 列表
     * @param timestamp 时间戳,如果从最新的获取, 此值为0
     * @param size      每人获取的条数
     * @return feed流ID列表
     */
    List<Long> batchLoadOutbox(List<Long> userIds, long timestamp, int size);

    /**
     * 根据用户 userId, 获取该用户的feed流
     *
     * @param userId    用户ID
     * @param timestamp 时间戳
     * @param size      获取的条数
     * @return feed流ID列表
     */
    List<Long> loadInbox(long userId, long timestamp, int size);
}
