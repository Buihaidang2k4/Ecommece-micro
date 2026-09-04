package com.example.MyShop_API.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class test implements CommandLineRunner {
    @Value("${DBMS_CONNECTION:NOT_FOUND}")
    private String dbConnection;

    @Value("${DBMS_USERNAME:NOT_FOUND}")
    private String dbusername;

    @Value("${DBMS_PASSWORD:NOT_FOUND}")
    private String dbpassword;

    // Redis-related environment variables
    @Value("${SPRING_DATA_REDIS_HOST:NOT_FOUND}")
    private String redisHost;

    @Value("${SPRING_DATA_REDIS_PORT:NOT_FOUND}")
    private String redisPort;

    @Value("${SPRING_DATA_REDIS_TIMEOUT:NOT_FOUND}")
    private String redisTimeout;

    @Value("${SPRING_REDIS_TYPE:NOT_FOUND}")
    private String redisType;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== TEST ENV ======");
        System.out.println("DBMS_CONNECTION = " + dbConnection);
        System.out.println("DBMS_USERNAME   = " + dbusername);
        System.out.println("DBMS_PASSWORD   = " + dbpassword);
        System.out.println("--- REDIS CONFIG ---");
        System.out.println("REDIS_HOST      = " + redisHost);
        System.out.println("REDIS_PORT      = " + redisPort);
        System.out.println("REDIS_TIMEOUT   = " + redisTimeout);
        System.out.println("REDIS_TYPE      = " + redisType);
        System.out.println("======================");
    }

}
