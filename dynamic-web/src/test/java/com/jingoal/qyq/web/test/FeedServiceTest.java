package com.jingoal.qyq.web.test;

import com.jingoal.qyq.common.Feed;
import com.jingoal.qyq.web.service.FeedService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by lianghb on 16/3/23.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-service.xml"})
public class FeedServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private FeedService feedService;

    @Test
    public void addTest() {
        Feed feed = new Feed();
        feed.setCid(1000);
        feed.setId(1);
        feed.setContent("feed-1");
        feed.setTime(new Date("2016-01-01 22:20:10").getTime());
        feedService.add(feed);
    }

    @Test
    public void mergeTest() {

    }
}