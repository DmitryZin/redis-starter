package ru.spbe.redisstarter;

public interface IRedisSubscriber{
    void onMessage(String message);
}
