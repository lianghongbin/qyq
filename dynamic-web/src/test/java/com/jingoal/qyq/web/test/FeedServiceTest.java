package com.jingoal.qyq.web.test;

import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.common.Row;
import com.jingoal.qyq.web.service.FeedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by lianghb on 16/3/23.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-redis.xml"})
public class FeedServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private FeedService feedService;

    @Test
    public void addTest() {
        Feed feed = new Feed();
        feed.setCid(3000);
        feed.setId(140);
        feed.setContent("feed-140");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 12, 12);
        feed.setTime(calendar.getTimeInMillis());
        feedService.addOutbox(feed);
    }

    @Test
    public void mergeTest() {

        Set<Feed> feeds = new HashSet<>(2);
        Feed feed = new Feed();
        feed.setCid(3000);
        feed.setId(8);
        feed.setContent("feed-8");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        feed.setTime(calendar.getTimeInMillis());

        feeds.add(feed);

        Feed feed1 = new Feed();
        feed1.setCid(2000);
        feed1.setId(9);
        feed1.setContent("feed-27");

        calendar.set(2015, 9, 27);
        feed1.setTime(calendar.getTimeInMillis());
        feeds.add(feed1);

        feedService.merge(1000, feeds);
    }

    @Test
    public void batchLoadOutboxTest() {
        List<Long> ids = new ArrayList<>();
        ids.add(1000L);
        ids.add(2000L);
        ids.add(3000L);
        ids.add(4000L);

        Set<Row> result = feedService.batchLoadOutbox(ids, 0, 20);
        System.out.println(result.size());

        for (Row row : result) {
            System.out.println(row.getScore() + "--" + row.getElement());
        }
        Assert.isTrue(result.size() > 0);
    }

    @Test
    public void loadOutboxTest() {

        Set<Row> result = feedService.loadOutbox(1000, 0, 2);
        System.out.println(result.size());
        for (Row row : result) {
            System.out.println(row.getScore() + "--" + row.getElement());
        }

        Assert.isTrue(result.size() > 0);
    }

    @Test
    public void mergeDateTest() {

        long start = new Date().getTime();
        Map<Long, Feed> data = new HashMap<>(10000);
        Feed feed = new Feed();
        feed.setId(1100);
        feed.setTime(145000000L);
        for (long i=1;i<10000;i++) {
            data.put(i, feed);
        }

        feedService.merge(data);

        System.out.println(new Date().getTime() - start);
    }
}