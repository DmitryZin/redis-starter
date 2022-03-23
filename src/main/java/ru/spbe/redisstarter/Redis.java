package ru.spbe.redisstarter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Set;

/**
 * Класс {@link Redis} реализует сервис по работе с данными БД Redis
 * Бин заказывается настройкой redis.enabled = true
 * Требует @Bean jedis с уже настроенным подключением к БД
 * redis.host = ... redis.port = ....
 *
 */
@Slf4j
@RequiredArgsConstructor
public class Redis {
    private final Jedis jedis;

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
     * Отправляет сообщения подписчика
     * @param channel имя топика
     * @param message сообщение
     */
    public void publishMessage(String channel, String message){
        jedis.publish(channel, message);
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
