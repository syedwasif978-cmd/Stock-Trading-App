package com.stockapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.stockapp", "com.stockapp" })
public class StockTradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTradingApplication.class, args);
        System.out.println("========================================");
        System.out.println("Stock Trading Application Started!");
        System.out.println("Open your browser and navigate to:");
        System.out.println("http://localhost:8080");
        System.out.println("========================================");
    }
}
