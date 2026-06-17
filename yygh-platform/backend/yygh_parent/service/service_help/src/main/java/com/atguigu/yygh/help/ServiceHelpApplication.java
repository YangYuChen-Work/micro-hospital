package com.atguigu.yygh.help;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
@EnableDiscoveryClient
public class ServiceHelpApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHelpApplication.class, args);
    }
}
