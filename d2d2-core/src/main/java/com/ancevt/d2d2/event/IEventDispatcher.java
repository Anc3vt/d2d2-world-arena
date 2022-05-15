
package com.ancevt.d2d2.event;

public interface IEventDispatcher {

    void addEventListener(String type, EventListener listener);

    void addEventListener(String type, EventListener listener, boolean reset);

    //void addEventListenerByKey(Object key, String type, EventListener listener);

    //void addEventListenerByKey(Object key, String type, EventListener listener, boolean reset);

    void addEventListener(Object owner, String type, EventListener listener);

    void addEventListener(Object owner, String type, EventListener listener, boolean reset);

    void removeEventListener(String type, EventListener listener);

    //void removeEventListenerByKey(Object key);

    void removeEventListener(Object owner, String type);

    void dispatchEvent(Event<?> event);

    void removeAllEventListeners(String type);

    void removeAllEventListeners();
}
