package com.master4uall.spring.social.twitter.service;

public interface TwitterService {

    void watch(String query);

    void search(String query);

    void searchUserById(String id);

    void findFollowerByUserId(String id);

    void findFollowerLocationByUserId(String id);

    void findFriendsLocationByUserId(String id);

    int followerCountByName(String userName);
}
