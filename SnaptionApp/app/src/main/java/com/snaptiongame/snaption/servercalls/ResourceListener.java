package com.snaptiongame.snaption.servercalls;

/**
 * Created by jason_000 on 1/12/2017.
 */

public interface ResourceListener<T> {
    /**
     * onData is called whenever a resource has been retrieved from the server.
     * @param data The data retrieved by the server of the generic type specified
     */
    void onData(T data);

    /**
     * getDataType should return the class type of the resource to be retrieved.
     * If a List of resources are to be retrieved, the class type of the resources
     * should be returned.
     * @return The class type of the resource
     */
    Class getDataType();
}
