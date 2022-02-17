package ru.ancevt.d2d2.event;

import ru.ancevt.d2d2.display.IDisplayObjectContainer;

public class Event {

    public static final String EACH_FRAME = "eachFrame";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String ADD_TO_STAGE = "addToStage";
    public static final String REMOVE_FROM_STAGE = "removeFromStage";
    public static final String COMPLETE = "complete";
    public static final String RESIZE = "resize";
    public static final String CHANGE = "change";

    String type;
    IEventDispatcher source;
    private IDisplayObjectContainer parent;

    public Event(String type, IEventDispatcher source) {
        this.type = type;
        this.source = source;
    }

    public Event(String type, IEventDispatcher source, IDisplayObjectContainer parent) {
        this(type, source);
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public IEventDispatcher getSource() {
        return source;
    }

    public IDisplayObjectContainer getParent() {
        return parent;
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
