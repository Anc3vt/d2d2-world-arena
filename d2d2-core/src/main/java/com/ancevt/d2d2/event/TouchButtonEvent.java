
package com.ancevt.d2d2.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TouchButtonEvent extends Event {

    public static final String TOUCH_DOWN = "touchDown";
    public static final String TOUCH_UP = "touchUp";
    public static final String TOUCH_DRAG = "touchDrag";
    public static final String TOUCH_HOVER = "touchHover";
    public static final String TOUCH_HOVER_OUT = "touchHoverOut";

    private final int x;
    private final int y;
    private final int mouseButton;
    private final boolean leftMouseButton;
    private final boolean rightMouseButton;
    private final boolean middleMouseButton;
    private final boolean onArea;
}
