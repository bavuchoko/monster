package com.example.monster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = { "classpath:jdbc.properties" })
public class MonsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonsterApplication.class, args);
    }


}
