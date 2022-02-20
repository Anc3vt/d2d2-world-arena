/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2world.desktop.ui.chat;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.Holder;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObject;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.ScaleMode;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2world.desktop.ui.Font;
import ru.ancevt.d2d2world.desktop.ui.TextInputEvent;
import ru.ancevt.d2d2world.desktop.ui.TextInputProcessor;
import ru.ancevt.d2d2world.desktop.ui.UiTextInput;
import ru.ancevt.util.repl.ReplInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat extends DisplayObjectContainer {

    private static final int MAX_MESSAGES = 100;

    private static final float DEFAULT_WIDTH = 900.0f / 2.0f;
    private static final float DEFAULT_HEIGHT = 600.0f / 3.0f;
    private static final int INPUT_MAX_LENGTH = 80;

    private final UiTextInput input;
    private final List<ChatMessage> messages;
    private final List<ChatMessage> displayedMessages;
    private final List<String> history;
    private float width;
    private float height;
    private int scroll;
    private int lastChatMessageId;
    private boolean shadowEnabled;
    private int historyIndex;

    public Chat() {
        input = new UiTextInput();
        messages = new CopyOnWriteArrayList<>();
        displayedMessages = new CopyOnWriteArrayList<>();
        history = new ArrayList<>();
        shadowEnabled = true;

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;

        input.setWidth(20);
        input.addEventListener(TextInputEvent.TEXT_ENTER, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_CHANGE, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_INPUT_KEY_DOWN, this::textInputEvent);

        redraw();
    }

    public void setShadowEnabled(boolean b) {
        this.shadowEnabled = b;
        messages.forEach(m -> m.setShadowEnabled(b));
    }

    public boolean getShadowEnabled() {
        return shadowEnabled;
    }

    public int getLastChatMessageId() {
        return lastChatMessageId;
    }

    public void setWidth(float width) {
        this.width = width;
        redraw();
    }

    public void setHeight(float height) {
        this.height = height;
        redraw();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        redraw();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    private void redraw() {
        // TODO: implement scroll

        input.setXY(0, height - input.getHeight());

        displayedMessages.forEach(DisplayObject::removeFromParent);

        int y = 0;

        for (int i = scroll; i < messages.size() && i - scroll < getMessageCountOnDisplay(); i++) {
            ChatMessage chatMessage = messages.get(i);
            displayedMessages.add(chatMessage);

            add(chatMessage, 0, y);
            y += chatMessage.getHeight();
        }
    }

    private int getMessageCountOnDisplay() {
        return (int) ((getHeight() - input.getHeight()) / ChatMessage.DEFAULT_HEIGHT);
    }

    public void setScroll(int scroll) {
        if (this.scroll == scroll) return;

        if (scroll > messages.size() - getMessageCountOnDisplay()) {
            scroll = messages.size() - getMessageCountOnDisplay();
        }
        if (scroll < 0) scroll = 0;

        this.scroll = scroll;

        redraw();
    }

    public int getScroll() {
        return scroll;
    }

    public void addServerMessage(int chatMessageId, @NotNull String messageText, Color textColor) {
        addMessage(new ChatMessage(chatMessageId, messageText, textColor));
        redraw();
    }

    public void addPlayerMessage(int id,
                                 int playerId,
                                 @NotNull String playerName,
                                 int playerColor,
                                 @NotNull String messageText,
                                 @NotNull Color textColor) {
        addMessage(new ChatMessage(id, playerId, playerName, playerColor, messageText, textColor));
        if (id != 0) lastChatMessageId = id;
        redraw();
    }

    public void addMessage(@NotNull String messageText, @NotNull Color textColor) {
        addMessage(new ChatMessage(0, messageText, textColor));
        redraw();
    }

    private void addMessage(@NotNull ChatMessage chatMessage) {
        chatMessage.setShadowEnabled(getShadowEnabled());
        messages.add(chatMessage);

        if (messages.size() > MAX_MESSAGES) {
            messages.removeAll(messages.subList(0, MAX_MESSAGES / 4));
        }

        scrollToEnd();
        redraw();
    }

    private void scrollToEnd() {
        setScroll(Integer.MAX_VALUE);
    }

    public void openInput() {
        add(input);
        dispatchEvent(new ChatEvent(ChatEvent.CHAT_INPUT_OPEN, this, null));
    }

    public void closeInput() {
        remove(input);
        dispatchEvent(new ChatEvent(ChatEvent.CHAT_INPUT_CLOSE, this, null));
    }

    public boolean isInputOpened() {
        return input.hasParent();
    }

    public void clear() {
        messages.clear();
        redraw();
    }

    // TODO: split to 3 methods
    public void textInputEvent(Event event) {
        if (event instanceof TextInputEvent textInputEvent) {
            switch (event.getType()) {

                case TextInputEvent.TEXT_CHANGE -> {
                    String text = textInputEvent.getText();
                    int length = text.length();
                    if(length > INPUT_MAX_LENGTH) {
                        input.setText(text.substring(0, INPUT_MAX_LENGTH));
                        return;
                    }
                    int w = text.length() * Font.getBitmapFont().getCharInfo('0').width();
                    input.setWidth(w + 20);
                }

                case TextInputEvent.TEXT_ENTER -> {
                    String text = textInputEvent.getText();
                    if (!text.isBlank()) {
                        dispatchEvent(new ChatEvent(ChatEvent.CHAT_TEXT_ENTER, this, text));
                        history.add(text);
                        historyIndex = history.size();
                    }
                    input.clear();
                    closeInput();
                }

                case TextInputEvent.TEXT_INPUT_KEY_DOWN -> {
                    switch (textInputEvent.getKeyCode()) {
                        case KeyCode.UP -> {
                            if(historyIndex == history.size()) {
                                history.add(input.getText());
                            }
                            historyIndex--;
                            setTextFromPlayerChatMessageHistory();
                        }
                        case KeyCode.DOWN -> {
                            historyIndex++;
                            setTextFromPlayerChatMessageHistory();
                        }
                        case KeyCode.ESCAPE -> {
                            closeInput();
                        }
                    }
                }
            }
        }
    }

    private void setTextFromPlayerChatMessageHistory() {
        if (historyIndex > history.size() - 1) {
            input.moveCaretToEnd();
            historyIndex = history.size() - 1;
        }

        if (historyIndex < 0) {
            historyIndex = 0;
            return;
        }

        input.setText(history.get(historyIndex));
        input.moveCaretToEnd();
    }


    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();
        root.setBackgroundColor(Color.BLUE);

        Holder<Integer> idCounter = new Holder<>(1);

        Chat chat = new Chat();
        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            if (event instanceof ChatEvent chatEvent) {
                String text = chatEvent.getText();
                idCounter.setValue(idCounter.getValue() + 1);
                chat.addPlayerMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, text, Color.WHITE);
            }
        });
        root.add(chat, 10, 10);

        for (int i = 0; i < 10; i++) {
            idCounter.setValue(idCounter.getValue() + 1);
            chat.addPlayerMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, "Hello, i'm Ancevt" + i, Color.WHITE);
        }

        ReplInterpreter repl = new ReplInterpreter();
        repl.addCommand("p", a -> {
            idCounter.setValue(idCounter.getValue() + 1);
            int playerId = a.get(int.class, new String[]{"-p"}, 0);
            String playerName = a.get(String.class, new String[]{"-n"}, "Name");
            int playerColor = a.get(int.class, new String[]{"-c"}, 0xFFFF00);
            String messageText = a.get(String.class, new String[]{"-m"});
            chat.addPlayerMessage(idCounter.getValue(), playerId, playerName, playerColor, messageText, Color.WHITE);
        });

        repl.addCommand("s", a -> {
            idCounter.setValue(idCounter.getValue() + 1);
            String messageText = a.get(String.class, new String[]{"-m"});
            chat.addServerMessage(idCounter.getValue(), messageText, Color.GRAY);
        });
        new Thread(repl::start).start();

        TextInputProcessor.enableRoot(root);
        root.addEventListener(InputEvent.KEY_DOWN, event -> {
            InputEvent inputEvent = (InputEvent) event;
            switch (inputEvent.getKeyCode()) {
                case KeyCode.PAGE_UP -> {
                    chat.setScroll(chat.getScroll() - 10);
                }

                case KeyCode.PAGE_DOWN -> {
                    chat.setScroll(chat.getScroll() + 10);
                }

                case KeyCode.F8 -> {
                    chat.setShadowEnabled(!chat.getShadowEnabled());
                }

                case KeyCode.F6, KeyCode.T -> {
                    if (!chat.isInputOpened()) chat.openInput();
                }
            }
        });
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.loop();
    }

}
