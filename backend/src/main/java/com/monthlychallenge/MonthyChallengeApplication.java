package com.monthlychallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonthyChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonthyChallengeApplication.class, args);
    }
}
