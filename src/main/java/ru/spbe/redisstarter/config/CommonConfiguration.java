package ru.spbe.redisstarter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import ru.spbe.redisstarter.Redis;


@Configuration
public class CommonConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"redis.enabled"}, havingValue = "true")
    public Jedis jedis(
            @Value("${redis.host}") final String host,
            @Value("${redis.port}") final int port
    ) {
        Jedis jedis = new Jedis(host, port);
        jedis.connect();
        return jedis;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"redis.enabled"}, havingValue = "true")
    public Redis redis(Jedis jedis){
        return new Redis(jedis);
    }
}
