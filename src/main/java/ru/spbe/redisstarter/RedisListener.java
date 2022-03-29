package ru.spbe.redisstarter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

/**
 * подключить "слушателя" сообщений
 *  * В данной реализации только одного (пока нет необходимости их делать несколько)
 *  * Слушатель должен имплиментировать интерфейс {@link IRedisSubscriber}
 *  Иметь канал, который слушает и метод, который вызывается, при получении сообщений
 */
@Slf4j
@RequiredArgsConstructor
public class RedisListener{

    private final Redis redis;
    private final IRedisSubscriber subscriber;
    private final RedisSet set;
    private JedisPubSub jedisPubSub;

    /**
     * отписаться от сообщений
     */
    public void unSubscribe(){
        redis.unSubscribe(jedisPubSub);
        jedisPubSub = null;
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
                subscriber.onMessage(message);
            }
        };

        redis.subscribe(jedisPubSub, set.getChannelName());
    }

}
