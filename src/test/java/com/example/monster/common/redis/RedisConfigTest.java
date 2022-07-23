package com.example.monster.common.redis;

import com.example.monster.common.BaseControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.stream.Collectors;


@Slf4j
@Configuration
class RedisConfigTest extends BaseControllerTest {


    private RedisServer redisServer;

    public void TestRedisConfig(@Value("${spring.redis.port}") int redisPort) {
        redisServer = new RedisServer(redisPort);
        log.info("Embedded Redis Object Created");
    }

    @PostConstruct
    public void startRedis() {
        redisServer.start();
        log.info("Embedded Redis Started");
        log.info(" >> Port: " + redisServer.ports().stream().map(n -> String.valueOf(n)).collect(Collectors.joining()));
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
        log.info("Embedded Redis Stopped");
    }
}