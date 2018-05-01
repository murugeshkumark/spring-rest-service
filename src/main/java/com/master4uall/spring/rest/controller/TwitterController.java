package com.master4uall.spring.rest.controller;

import com.master4uall.spring.social.twitter.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/twitter")
public class TwitterController {

    @Autowired
    TwitterService service;

    @RequestMapping("/followers")
    public String followers(@RequestParam(value="name" , required = true) String name) {
        return "No. of followers is "+service.followerCountByName(name);
    }
}
