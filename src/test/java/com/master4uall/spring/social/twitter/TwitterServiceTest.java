package com.master4uall.spring.social.twitter;

import com.master4uall.spring.social.twitter.service.TwitterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SocialConfig.class)
public class TwitterServiceTest {

    @Autowired
    TwitterService service;

    @Test
    public void watch() {
        service.watch("narendramodi");
    }

    @Test
    public void search() {
        service.search("rcb");
    }

    @Test
    public void searchUserById() {
        service.searchUserById("cricket");
    }

    @Test
    public void findFollowerByUserId() {
        service.findFollowerByUserId("master4uall");
    }

    @Test
    public void findFollowerLocationByUserId() {
        service.findFollowerLocationByUserId("master4uall");
    }

    @Test
    public void findFriendsLocationByUserId() {
        service.findFriendsLocationByUserId("master4uall");
    }

    @Test
    public void findFollowersByUserId() {
        service.findFollowersByUserId("OviyaaSweetz");
    }
}