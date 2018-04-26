package com.master4uall.spring.rest.controller;

import com.master4uall.spring.rest.bean.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String GREETING_TEMPLATE = "Hello, %s!";
    private static final String BYE_TEMPLATE = "Bye, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(GREETING_TEMPLATE, name));
    }

    @RequestMapping("/bye")
    public Greeting bye(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(BYE_TEMPLATE, name));
    }
}
