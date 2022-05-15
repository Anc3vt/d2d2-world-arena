
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UiTextInputEvent extends Event<UiTextInput> {

    public static final String TEXT_CHANGE = "textChange";
    public static final String TEXT_ENTER = "textEnter";
    public static final String TEXT_INPUT_KEY_DOWN = "textInputKeyDown";

    private final String text;
    private final int keyCode;
}
