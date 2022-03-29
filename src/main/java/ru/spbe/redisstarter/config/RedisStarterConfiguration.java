package ru.spbe.redisstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbe.redisstarter.Redis;


@Configuration
public class RedisStarterConfiguration {
    @Bean
    public Redis redis(@Value("${redis.host}") final String host,
                       @Value("${redis.port}") final int port){
        return new Redis(host, port);
    }

    @Bean
    public ObjectMapper mapper(){
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
