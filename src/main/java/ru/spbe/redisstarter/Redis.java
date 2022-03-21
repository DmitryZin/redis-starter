package ru.spbe.redisstarter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

/**
 * Класс {@link Redis} реализует сервис по работе с данными БД Redis
 * Бин заказывается настройкой redis.enabled = true
 * Требует @Bean jedis с уже настроенным подключением к БД
 * redis.host = ... redis.port = ....
 *
 * Есть возможность подключить "слушателя" сообщений
 * В данной реализации только одного (пока нет необходимости их делать несколько)
 * Слушатель должен имплиментировать интерфейс {@link IRedisSubscriber}
 */
@Slf4j
@RequiredArgsConstructor
public class Redis {
    private final Jedis jedis;

    @Setter
    @Getter
    private IRedisSubscriber subscriber;

    private JedisPubSub jedisPubSub;

    /**
     * Возвращает все значения множества
     * @param dataSetName  имя множества
     * @return  содержимое множества
     */
    public Set<String> getDataSet(String dataSetName) {
        return jedis.smembers(dataSetName);
    }

    /**
     * Добавляет значение в множество (добавится, только если отсутствует)
     * @param dataSetName  имя множества
     * @param value  добавляемое значение
     */
    public void addValue(String dataSetName, String value){
        jedis.sadd(dataSetName, value);
    }

    /**
     * Удаляет значение из множества
     * @param dataSetName имя множества
     * @param value удаляемое значение
     */
    public void delValue(String dataSetName, String value){
        jedis.srem(dataSetName, value);
    }

    /**
     * Добавляет значения в множество (добавятся только отсутствующие)
     * @param dataSetName  имя множества
     * @param value  добавляемые значения
     */
    public void addValues(String dataSetName, Set<String> value){
        for (String s : value) {
            addValue(dataSetName, s);
        }
    }

    /**
     * Очищает ключ/множеств
     * @param dataSetName имя ключа/множества
     */
    public void clearDataSet(String dataSetName){
        jedis.del(dataSetName);
    }

    /**
     * Отправляет сообщения подписчика
     * @param topicName имя топика
     * @param message сообщение
     */
    public void publishMessage(String topicName, String message){
        jedis.publish(topicName, message);
    }

    /**
     * Подписаться на сообщения - создать "слушателя"
     */
    public void subscribe(){
        if(subscriber == null){
            log.info("subscriber is null");
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
     * Установить обычное ключ-значение в БД
     * @param keyName ключ
     * @param value значение
     */
    public void setKeyValue(String keyName, String value) {
        jedis.set(keyName, value);
    }

    /**
     * Получить обычное ключ-значеие из БД
     * @param keyName ключ
     * @return значение ключа
     */
    public String getKeyValue(String keyName) {
        return jedis.get(keyName);
    }

    /**
     * Очистить БД
     */
    public void flushDB(){
        jedis.flushDB();
    }

}
