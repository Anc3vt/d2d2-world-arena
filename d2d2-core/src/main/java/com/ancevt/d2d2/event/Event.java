
package com.ancevt.d2d2.event;

import com.ancevt.d2d2.display.IDisplayObjectContainer;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Event<E extends IEventDispatcher> {

    public static final String EACH_FRAME = "eachFrame";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String ADD_TO_STAGE = "addToStage";
    public static final String REMOVE_FROM_STAGE = "removeFromStage";
    public static final String COMPLETE = "complete";
    public static final String RESIZE = "resize";
    public static final String CHANGE = "change";

    String type;
    E source;
    private IDisplayObjectContainer parent;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSource(E source) {
        this.source = source;
    }

    public E getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", source=" + source +
                ", parent=" + parent +
                '}';
    }
}
