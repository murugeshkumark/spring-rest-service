package com.master4uall.spring.social.twitter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@ComponentScan("com.master4uall.spring.social")
@PropertySource(value = { "classpath:application.properties" })
public class SocialConfig {

    @Bean
    public TwitterTemplate twitterTemplate(@Value("${consumer.key}") String consumerKey,
                                           @Value("${consumer.secret}") String consumerSecret,
                                           @Value("${access.token}") String accessToken,
                                           @Value("${access.token.secret}") String accessTokenSecret) {
        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }
}