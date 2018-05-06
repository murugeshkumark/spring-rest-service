package com.master4uall.spring.social.twitter.service.impl;

import com.google.common.collect.Lists;
import com.master4uall.spring.social.twitter.PrintStreamListener;
import com.master4uall.spring.social.twitter.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.RateLimitExceededException;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.FilterStreamParameters;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
        BiFunction<String, Long, CursoredList<TwitterProfile>> handler = (String id1, Long l) -> twitterTemplate.friendOperations().getFollowersInCursor(id1, l);
        findByIdAndFunction(id, handler);
    }

    @Override
    public void findFriendsLocationByUserId(String id) {
        BiFunction<String, Long, CursoredList<TwitterProfile>> handler = (String id1, Long l) -> twitterTemplate.friendOperations().getFriendsInCursor(id1, l);
        findByIdAndFunction(id, handler);
    }

    @Override
    public void findFollowersByUserId(String id) {
        Map<String, AtomicInteger> locationCountMap = new HashMap<>();
        BiFunction<String, Long, CursoredList<Long>> handler = (id1, nextCur) -> twitterTemplate.friendOperations().getFollowerIdsInCursor(id1, nextCur);
        CursoredList<Long> followerIds;
        long nextCursor = -1;
        try {
            do {
                followerIds = handler.apply(id, nextCursor);
                findFollowersCountByLocation(followerIds)
                        .forEach((key, value) ->
                                locationCountMap.merge(key, value, (atomicInteger, atomicInteger2) -> new AtomicInteger(atomicInteger.addAndGet(atomicInteger2.get()))));
                nextCursor = followerIds.getNextCursor();
            }
            while (nextCursor > 0);
        } catch (RateLimitExceededException e) {
            throw e;
        } finally {
            //int total = locationCountMap.values().stream().mapToInt(value -> value.get()).sum();
            locationCountMap.entrySet().stream().sorted(Comparator.comparingInt(o -> o.getValue().get())).forEach((entry) -> System.out.println(entry.getKey() + "->" + entry.getValue()));
        }
    }

    private Map<String, AtomicInteger> findFollowersCountByLocation(CursoredList<Long> followerIds) {
        int maxSize = 100;
        List<List<Long>> followersSubLists = Lists.partition(followerIds, maxSize);
        Map<String, AtomicInteger> locationCountMap = new HashMap<>();
        long[] followersId;
        for (int j = 0; j < followersSubLists.size(); j++) {
            followersId = new long[followersSubLists.get(j).size()];
            for (int i = 0; i < followersId.length; i++)
                followersId[i] = followersSubLists.get(j).get(i);
            findLocations(followersId)
                    .forEach(locationKey -> {
                        locationCountMap
                                .computeIfAbsent(locationKey, key -> new AtomicInteger(0))
                                .getAndIncrement();
                    });
        }
        return locationCountMap;
    }

    private void findByIdAndFunction(String id, BiFunction<String, Long, CursoredList<TwitterProfile>> handler) {
        final String DEFAULT_LOCATION = "Unknown";
        Map<String, AtomicInteger> locationCountMap = new HashMap<>();
        CursoredList<TwitterProfile> twitterProfiles = handler.apply(id, (long) -1);
        try {
            while (!twitterProfiles.isEmpty()) {
                twitterProfiles.stream()
                        .map(twitterProfile -> StringUtils.isEmpty(twitterProfile.getLocation()) ? DEFAULT_LOCATION : twitterProfile.getLocation().trim())
                        .forEach(locationKey -> {
                            locationCountMap
                                    .computeIfAbsent(locationKey, key -> new AtomicInteger(0))
                                    .getAndIncrement();
                        });
                twitterProfiles = handler.apply(id, twitterProfiles.getNextCursor());
            }
        } catch (RateLimitExceededException e) {
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

    public List<String> findLocations(long... ids) {
        final String DEFAULT_LOCATION = "Unknown";
        List<TwitterProfile> twitterProfiles = twitterTemplate.userOperations().getUsers(ids);
        return twitterProfiles.stream()
                .map(twitterProfile -> StringUtils.isEmpty(twitterProfile.getLocation()) ? DEFAULT_LOCATION : twitterProfile.getLocation().trim())
                .collect(Collectors.toList());

    }
}
