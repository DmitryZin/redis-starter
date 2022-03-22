package ru.spbe.redisstarter;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * подключить "слушателя" сообщений
 *  * В данной реализации только одного (пока нет необходимости их делать несколько)
 *  * Слушатель должен имплиментировать интерфейс {@link IRedisSubscriber}
 *  Иметь канал, который слушает и метод, который вызывается, при получении сообщений
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSubscriber{
    private final Jedis jedis;

    @Setter
    @Getter
    private IRedisSubscriber subscriber;

    private JedisPubSub jedisPubSub;

    /**
     * отписаться от сообщений
     */
    public void unSubscribe(){
        if(jedisPubSub != null) {
            jedisPubSub.unsubscribe();
            jedisPubSub = null;
        }
    }

    /**
     * Подписаться на сообщения - создать "слушателя"
     */
    public void subscribe(){
        if(subscriber == null){
            log.info("subscriber is null");
            return;
        }
        if(jedisPubSub != null){
            log.info("I'm already subscribe");
            return;
        }
        jedisPubSub = new JedisPubSub(){
            @Override
            public void onMessage(String channel, String message){
                subscriber.onMessage(channel, message);
            }
        };

        new Thread(() -> {
            try{
                jedis.subscribe(jedisPubSub, subscriber.CHANNEL_NAME);
                log.info("Subscription success.");
            }
            catch(Exception e){
                log.error("Subscribing failed.", e);
            }
        }).start();
    }

}
