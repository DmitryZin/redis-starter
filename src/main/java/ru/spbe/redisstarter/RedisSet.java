package ru.spbe.redisstarter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Интерфейс, описывающий множество в RedisDB
 * DATASET_NAME - именование множества
 * CHANNEL_NAME - имя канала для сообщений
 */
@RequiredArgsConstructor
public class RedisSet {
    @Getter
    private final String dataSetName;
    @Getter
    private final String channelName;
}
