package com.snaptiongame.snaptionapp;

/**
 * Created by jason_000 on 1/12/2017.
 */

public interface MessageUpdater<T> {
    void onUpdate(T test);
    Class getDataType();
}
