package com.tsuki.tester.testspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-03-18 10:47
 **/
@Configuration
public class AppRunner {

    @Bean
    public GreetingService greetingService() {
        return new GreetingService();
    }

    @Bean
    public LocalDateTime appStartTime(){
        return LocalDateTime.now();
    }

    @Bean
    public Greeter greeter() {
        return new Greeter();
    }

    public static void main(String... strings) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppRunner.class);
        Greeter greeter = context.getBean(Greeter.class);
        greeter.showGreeting("Joe");
    }
}
