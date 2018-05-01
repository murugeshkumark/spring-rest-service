package com.master4uall.spring.social.twitter.service.impl;

import com.master4uall.spring.social.twitter.PrintStreamListener;
import com.master4uall.spring.social.twitter.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.FilterStreamParameters;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TwitterServiceImpl implements TwitterService {

    @Autowired
    TwitterTemplate twitterTemplate;

    @Override
    public void watch(String query) {
        List<StreamListener> streamListeners = new ArrayList<>();
        PrintStreamListener streamListener = new PrintStreamListener();
        streamListeners.add(streamListener);
        twitterTemplate.streamingOperations().filter(query, streamListeners);
        try {
            Thread.sleep(300 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FilterStreamParameters flterStreamParameters = new FilterStreamParameters();
        float west = 81.2707f;
        float south = 12.0827f;
        float east = 80.2707f;
        float north = 13.0827f;
        flterStreamParameters.addLocation(west, south, east, north);
        twitterTemplate.streamingOperations().filter(flterStreamParameters, streamListeners);
    }

    @Override
    public void search(String query) {
        twitterTemplate.searchOperations().search(query).getTweets().forEach(tweet -> System.out.println(tweet.getText()));
    }

    @Override
    public void searchUserById(String id) {
        List<TwitterProfile> twitterProfiles = twitterTemplate.userOperations().searchForUsers(id, 2, 30);
        System.out.println("No. of results found " + twitterProfiles.size());
        twitterProfiles.forEach(twitterProfile -> {
            System.out.println(twitterProfile.getScreenName());
        });
    }

    @Override
    public void findFollowerByUserId(String id) {
        CursoredList<TwitterProfile> twitterProfiles = twitterTemplate.friendOperations().getFollowers(id);
        AtomicInteger i = new AtomicInteger();
        while (!twitterProfiles.isEmpty()) {
            twitterProfiles.forEach(twitterProfile -> {
                System.out.println((i.getAndIncrement()) + ":" + twitterProfile.getScreenName());
            });
            twitterProfiles = twitterTemplate.friendOperations().getFollowersInCursor(id, twitterProfiles.getNextCursor());
        }
    }

    @Override
    public void findFollowerLocationByUserId(String id) {
        Map<String, AtomicInteger> locationCountMap = new HashMap<>();
        CursoredList<TwitterProfile> twitterProfiles = twitterTemplate.friendOperations().getFollowers(id);
        while (!twitterProfiles.isEmpty()) {
            twitterProfiles.forEach(twitterProfile -> {
                locationCountMap.computeIfAbsent(StringUtils.isEmpty(twitterProfile.getLocation()) ? "Unknown" : twitterProfile.getLocation(), key -> new AtomicInteger(0)).getAndIncrement();
            });
            twitterProfiles = twitterTemplate.friendOperations().getFollowersInCursor(id,
                    0);
//                    twitterProfiles.getNextCursor());
        }
        locationCountMap.entrySet().forEach((entry) -> System.out.println(entry.getKey() + "->" + entry.getValue()));
    }

    @Override
    public void findFriendsLocationByUserId(String id) {
        Map<String, AtomicInteger> locationCountMap = new HashMap<>();
        CursoredList<TwitterProfile> twitterProfiles = twitterTemplate.friendOperations().getFriendsInCursor(id, -1);
        try {
            while (!twitterProfiles.isEmpty()) {
                twitterProfiles.forEach(twitterProfile -> {
                    locationCountMap.computeIfAbsent(StringUtils.isEmpty(twitterProfile.getLocation()) ? "Unknown" : twitterProfile.getLocation(), key -> new AtomicInteger(0)).getAndIncrement();
                });
                twitterProfiles = twitterTemplate.friendOperations().getFriendsInCursor(id, twitterProfiles.getNextCursor());
            }
        } catch (org.springframework.social.RateLimitExceededException e) {
            System.out.println("Next cursor is " + twitterProfiles.getNextCursor());
            throw e;
        } finally {
            locationCountMap.entrySet().forEach((entry) -> System.out.println(entry.getKey() + "->" + entry.getValue()));
        }

    }

    @Override
    public int followerCountByName(String userName) {
        List<TwitterProfile> users = twitterTemplate.userOperations().searchForUsers(userName);
        if (users.size() == 1) {
            return users.get(0).getFollowersCount();
        }
        return 0;
    }
}
