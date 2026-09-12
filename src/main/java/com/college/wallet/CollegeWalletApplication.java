package com.college.wallet;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableScheduling
@SpringBootApplication
public class CollegeWalletApplication {

    public static void main(String[] args) {
        // Use SpringApplication (the class), not the annotation name
        SpringApplication.run(CollegeWalletApplication.class, args);
        
        System.out.println("------------------------------------");
        System.out.println("🚀 CAMPUS CREDIT ENGINE IS LIVE!");
        System.out.println("------------------------------------");
    }
}