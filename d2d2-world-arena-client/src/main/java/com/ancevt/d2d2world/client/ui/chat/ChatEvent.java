
package com.ancevt.d2d2world.client.ui.chat;

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ChatEvent extends Event {

    public static final String CHAT_TEXT_ENTER = "chatTextEnter";
    public static String CHAT_INPUT_OPEN = "chatInputOpen";
    public static String CHAT_INPUT_CLOSE = "chatInputClose";

    private final String text;
}
