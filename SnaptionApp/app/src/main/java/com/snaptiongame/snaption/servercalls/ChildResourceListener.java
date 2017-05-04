package com.snaptiongame.snaption.servercalls;

/**
 * Created by Jason Krein on 4/16/2017.
 * Listener that includes callbacks for child events
 * Add the other events if they are needed.
 */

public interface ChildResourceListener<T> {
    /**
     * onData is called whenever a resource has been retrieved from the server.
     * @param data The data retrieved by the server of the generic type specified
     */
    void onNewData(T data);

    /**
     * Called when data in the path is changed
     * @param data the changed data
     */
    void onDataChanged(T data);

    /**
     * getDataType should return the class type of the resource to be retrieved.
     * If a List of resources are to be retrieved, the class type of the resources
     * should be returned.
     * @return The class type of the resource
     */
    Class getDataType();
}
