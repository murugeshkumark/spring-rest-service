package com.master4uall.spring.social.twitter;

import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintStreamListener implements StreamListener {


    @Override
    public void onTweet(Tweet tweet) {
        System.out.println("\n"+tweet.getFromUser() + "("+tweet.getUser().getTimeZone()+","+tweet.getUser().getFollowersCount()+") \n" + tweet.getText());
    }

    @Override
    public void onDelete(StreamDeleteEvent streamDeleteEvent) {

    }

    @Override
    public void onLimit(int i) {

    }

    @Override
    public void onWarning(StreamWarningEvent streamWarningEvent) {

    }
}
