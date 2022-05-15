
package com.ancevt.d2d2.event;

import com.ancevt.d2d2.display.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InputEvent extends Event<Root> {

    public static final String BACK_PRESS = "backPress";
    public static final String MOUSE_MOVE = "mouseMove";
    public static final String MOUSE_DOWN = "mouseDown";
    public static final String MOUSE_UP = "mouseUp";
    public static final String MOUSE_WHEEL = "mouseWheel";
    public static final String KEY_DOWN = "keyDown";
    public static final String KEY_UP = "keyUp";
    public static final String KEY_TYPE = "keyType";

    private final int x;
    private final int y;
    private final int mouseButton;
    private final int delta;
    private final boolean drag;
    private final int pointer;
    private final int keyCode;
    private final char keyChar;
    private final boolean shift;
    private final boolean control;
    private final boolean alt;
    private final String keyType;
    private final int codepoint;

    @Override
    public String toString() {
        return "InputEvent{" +
                "x=" + x +
                ", y=" + y +
                ", mouseButton=" + mouseButton +
                ", delta=" + delta +
                ", drag=" + drag +
                ", pointer=" + pointer +
                ", keyCode=" + keyCode +
                ", keyChar=" + keyChar +
                ", shift=" + shift +
                ", control=" + control +
                ", alt=" + alt +
                '}';
    }
}
