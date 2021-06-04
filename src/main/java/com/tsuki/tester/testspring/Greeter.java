package com.tsuki.tester.testspring;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-03-18 10:46
 **/
public class Greeter {

    private String greetingFormat;

    @Autowired
    public void configure1(GreetingService greetingService, LocalDateTime appStartTime) {
        System.out.println("wtf");
        greetingFormat = String.format("%s. This app is running since: %s%n", greetingService.getGreeting("<NAME>"),
                appStartTime.format(DateTimeFormatter.ofPattern("YYYY-MMM-d")));
    }

    public void showGreeting(String name) {
        System.out.printf(greetingFormat.replaceAll("<NAME>", name));
    }
}
