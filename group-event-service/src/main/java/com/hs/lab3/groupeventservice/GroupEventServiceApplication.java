package com.hs.lab3.groupeventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients(basePackages = "com.hs.lab3.groupeventservice.client")
public class GroupEventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupEventServiceApplication.class, args);
    }

}
