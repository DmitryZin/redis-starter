package ru.spbe.redisstarter;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;

import java.util.Map;
import java.util.Set;

/**
 * Класс {@link Redis} реализует сервис по работе с данными БД Redis
 * Обертка над классом Jedis, чтоб упростить подписку на каналы
 * и спрятать массовое добавление в одном пакете в простой метод
 */
@Slf4j
public class Redis {
    /**
     * хост сервера Redis
     */
    private final String host;
    /**
     * порт сервера Redis
     */
    private final int port;

    /**
     * сам обертываемый Jedis
     */
    private final Jedis jedis;

    public Redis(String host, int port) {
        this.host = host;
        this.port = port;
        jedis = new Jedis(host, port);
        jedis.connect();
    }

    /**
     * Очистить БД
     */
    public void flushDB(){
        jedis.flushDB();
    }

    /**
     * Установить обычное ключ-значение в БД
     * @param keyName ключ
     * @param value значение
     */
    public void addValueToKey(String keyName, String value) {
        jedis.set(keyName, value);
    }

    /**
     * Получить обычное ключ-значеие из БД
     * @param keyName ключ
     * @return значение ключа
     */
    public String getValueFromKey(String keyName) {
        return jedis.get(keyName);
    }

    /**
     * Удалить ключ-значеие из БД
     * @param keyName ключ
     */
    public void delValueFromKey(String keyName) {
        jedis.del(keyName);
    }


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
    public void addValueToDataSet(String dataSetName, String value){

        if (!jedis.isConnected())
            jedis.connect();
        jedis.sadd(dataSetName, value);
    }

    /**
     * Удаляет значение из множества
     * @param dataSetName имя множества
     * @param value удаляемое значение
     */
    public void delValueFromDataSet(String dataSetName, String value){
        jedis.srem(dataSetName, value);
    }

    /**
     * Добавляет значения в множество (добавятся только отсутствующие)
     * @param dataSetName  имя множества
     * @param value  добавляемые значения
     */
    public void addValuesToDataSet(String dataSetName, Set<String> value){
        Pipeline pipeline = jedis.pipelined();
        for (String s : value) {
            pipeline.sadd(dataSetName, s);
        }
        pipeline.sync();
    }

    /**
     * Очищает ключ/множеств
     * @param dataSetName имя ключа/множества
     */
    public void clearDataSet(String dataSetName){
        jedis.del(dataSetName);
    }

    /**
     * Получить справочник
     * @param dataSetName
     * из ножества ключ/значение
     */
    public Map<String, String> getKeyDataSet(String dataSetName){ return jedis.hgetAll(dataSetName); }

    /**
     * Получить значение из справочника по ключу
     * @param dataSetName имя справочника
     * @param key ключ
     * @return значение ключа из справочника
     */
    public String getValueFromKeyDataSet(String dataSetName, String key){
        return jedis.hget(dataSetName, key);
    }

    /**
     * Добавить значение в спровочник
     * @param dataSetName имя справочника
     * состоящего из ключ/значение
     */
    public void addValueToKeyDataSet(String dataSetName, String key, String value){
        jedis.hset(dataSetName, key, value);
    }

    /**
     * Добавить значения в спровочник
     * @param dataSetName имя справочника
     * состоящего из ключ/значение
     */
    public void addValuesToKeyDataSet(String dataSetName, Map<String, String> keyValue){
        jedis.hset(dataSetName, keyValue);
    }

    /**
     * Удалить значение из справочника по ключу
     * @param dataSetName имя справочника
     * @param key ключ
     */
    public void delValueFromKeyDataSet(String dataSetName, String key){
        jedis.hdel(dataSetName, key);
    }

    /**
     * Очищает ключ множество
     * @param dataSetName имя множества
     */
    public void clearKeyDataSet(String dataSetName){
        jedis.del(dataSetName);
    }

    /**
     * Отправляет сообщения подписчика
     * @param channel имя топика
     * @param message сообщение
     */
    public void publishMessage(String channel, String message){
        jedis.publish(channel, message);
    }

    /**
     * Подписаться на сообщения (например, обновлять локальные справочники)
     * @param jedisPubSub -- что делать при подписке
     * @param channel -- канал, на который подписываемся
     */
    public void subscribe(JedisPubSub jedisPubSub, String channel){
        new Thread(() -> {
            try{

                Jedis subscriberJedis = new Jedis(this.host, this.port);
                subscriberJedis.subscribe(jedisPubSub, channel);
                log.info("Subscription success.");
            }
            catch(Exception e){
                log.error("Subscribing failed.", e);
            }
        }).start();
    }

    /**
     * Отписаться
     * @param jedisPubSub отписываемый подписчик
     */
    public void unSubscribe(JedisPubSub jedisPubSub){
        if(jedisPubSub != null) {
            jedisPubSub.unsubscribe();
        }
    }

}
