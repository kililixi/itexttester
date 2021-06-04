package com.tsuki.tester.testspring;

import java.util.function.Function;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-03-18 10:46
 **/
public class GreetingService {

    public String getGreeting(String name) {
        return "Hi there, " + name;
    }

    public static void main(String[] args) {
        method();
    }

    public static void method() {
        int[] total = new int[1];
        System.out.println(total[0]);
        Runnable r = () -> total[0]++;
        r.run();

        System.out.println(total[0]);
    }
}
