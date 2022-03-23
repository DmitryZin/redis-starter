package ru.spbe.redisstarter;

public interface IRedisSubscriber extends IRedisSet{
    void onMessage(String message);
}
