package ru.spbe.redisstarter;

public interface IRedisSubscriber {
    void onMessage(String topicName, String message);
    String CHANNEL_NAME = null;
}
