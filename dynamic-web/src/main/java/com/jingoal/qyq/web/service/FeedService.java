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
     * @param userId 用户ID
     * @return 用户动态列表
     */
    List<Long> loadOutbox(long userId);

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
