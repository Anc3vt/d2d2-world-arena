/*
 *   D2D2 World Desktop
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
package ru.ancevt.d2d2world.game.ui.chat;

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
import ru.ancevt.d2d2world.game.ui.Font;
import ru.ancevt.d2d2world.game.ui.TextInputEvent;
import ru.ancevt.d2d2world.game.ui.TextInputProcessor;
import ru.ancevt.d2d2world.game.ui.UiTextInput;
import ru.ancevt.util.repl.ReplInterpreter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat extends DisplayObjectContainer {

    public static final Chat INSTANCE = new Chat();

    private static final int MAX_MESSAGES = 100;

    private static final float DEFAULT_WIDTH = 900.0f / 2.0f;
    private static final float DEFAULT_HEIGHT = 600.0f / 3.0f;

    private final UiTextInput input;
    private final List<ChatMessage> messages;
    private final List<ChatMessage> displayedMessages;
    private float width;
    private float height;
    private int scroll;
    private int lastChatMessageId;
    private boolean shadowEnabled;
    private int historyIndex;
    private int localPlayerId;
    private String localPlayerName;
    private String typedBefore;

    private Chat() {

        input = new UiTextInput();
        messages = new CopyOnWriteArrayList<>();
        displayedMessages = new CopyOnWriteArrayList<>();
        shadowEnabled = true;

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;

        typedBefore = "";

        input.setWidth(20);
        input.addEventListener(TextInputEvent.TEXT_ENTER, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_CHANGE, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_INPUT_KEY_DOWN, this::textInputEvent);

        redraw();
    }

    public void setLocalPlayerName(String localPlayerName) {
        this.localPlayerName = localPlayerName;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(int localPlayerId) {
        this.localPlayerId = localPlayerId;
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

    public void addMessage(String text) {
        addMessage(0, text);
    }

    public void addMessage(int id, int playerId, String playerName, int playerColor, String messageText) {
        addMessage(new ChatMessage(id, playerId, playerName, playerColor, messageText));
        if (id != 0) lastChatMessageId = id;
        redraw();
    }

    public void addMessage(int id, String messageText) {
        addMessage(new ChatMessage(id, messageText));
        if (id != 0) lastChatMessageId = id;
        redraw();
    }

    private void addMessage(ChatMessage chatMessage) {
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
                    int w = text.length() * Font.getBitmapFont().getCharInfo('0').width();
                    input.setWidth(w + 20);
                }
                case TextInputEvent.TEXT_ENTER -> {
                    String text = textInputEvent.getText();
                    if (!text.isBlank()) {
                        text = text.trim();
                        dispatchEvent(new ChatEvent(ChatEvent.CHAT_TEXT_ENTER, this, text));
                        historyIndex = 0;
                    }
                    input.clear();
                    closeInput();
                }
                case TextInputEvent.TEXT_INPUT_KEY_DOWN -> {
                    switch (textInputEvent.getKeyCode()) {
                        case KeyCode.UP -> {
                            typedBefore = input.getText();
                            historyIndex++;
                            setTextFromPlayerChatMessageHistory();
                        }
                        case KeyCode.DOWN -> {
                            historyIndex--;
                            if (historyIndex <= 0) input.setText(typedBefore);
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
        List<ChatMessage> localPlayerMessages = messages.stream()
                .filter(m -> m.isFromPlayer() &&
                        m.getPlayerId() == localPlayerId &&
                        m.getPlayerName().equals(localPlayerName))
                .sorted((m1, m2) -> Integer.compare(m2.getId(), m1.getId()))
                .toList();

        if (historyIndex >= localPlayerMessages.size()) historyIndex = localPlayerMessages.size();

        if (historyIndex <= 0) {
            input.moveCaretToEnd();
            historyIndex = 0;
            return;
        }

        input.setText(localPlayerMessages.get(historyIndex - 1).getText());
        input.moveCaretToEnd();
    }


    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();
        root.setBackgroundColor(Color.BLUE);

        Holder<Integer> idCounter = new Holder<>(1);

        Chat chat = new Chat();
        chat.setLocalPlayerId(1);
        chat.setLocalPlayerName("TestPlayer");
        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            if (event instanceof ChatEvent chatEvent) {
                String text = chatEvent.getText();
                idCounter.setValue(idCounter.getValue() + 1);
                chat.addMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, text);
            }
        });
        root.add(chat, 10, 10);

        for (int i = 0; i < 10; i++) {
            idCounter.setValue(idCounter.getValue() + 1);
            chat.addMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, "Hello, i'm Ancevt" + i);
        }

        ReplInterpreter repl = new ReplInterpreter();
        repl.addCommand("p", a -> {
            idCounter.setValue(idCounter.getValue() + 1);
            int playerId = a.get(int.class, new String[]{"-p"}, 0);
            String playerName = a.get(String.class, new String[]{"-n"}, "Name");
            int playerColor = a.get(int.class, new String[]{"-c"}, 0xFFFF00);
            String messageText = a.get(String.class, new String[]{"-m"});
            chat.addMessage(idCounter.getValue(), playerId, playerName, playerColor, messageText);
        });

        repl.addCommand("s", a -> {
            idCounter.setValue(idCounter.getValue() + 1);
            String messageText = a.get(String.class, new String[]{"-m"});
            chat.addMessage(idCounter.getValue(), messageText);
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
