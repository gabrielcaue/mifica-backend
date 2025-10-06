package com.mifica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.mifica.entity")
@EnableJpaRepositories(basePackages = "com.mifica.repository")
public class MificaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MificaApplication.class, args);
    }
}