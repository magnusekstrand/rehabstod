package se.inera.intyg.rehabstod.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@Profile("dev")
public class EmbeddedCacheConfiguration {

    @Bean
    public RedisServer redisServer() {
        try {
            RedisServer redisServer = new RedisServer(6379);
            redisServer.start();
            return redisServer;
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize embedded redis (dev profile only");
        }
    }
}
