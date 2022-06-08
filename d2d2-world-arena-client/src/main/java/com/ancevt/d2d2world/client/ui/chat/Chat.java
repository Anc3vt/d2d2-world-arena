/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.client.ui.chat;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.components.TextInput;
import com.ancevt.d2d2.components.TextInputEvent;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Chat extends Container {

    private static Chat instace;

    public static Chat getInstance() {
        return instace == null ? instace = new Chat() : instace;
    }

    private static final int MAX_MESSAGES = 100;

    private static final int INPUT_MAX_LENGTH = 100;

    private static final int ALPHA_TIME = 500;

    private int alphaTime = ALPHA_TIME;

    private final TextInput input;
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
        input = new TextInput();
        messages = new CopyOnWriteArrayList<>();
        displayedMessages = new CopyOnWriteArrayList<>();
        history = new ArrayList<>();
        shadowEnabled = true;

        width = D2D2.stage().getWidth() / 2.0f;
        height = D2D2.stage().getHeight() / 3.0f;

        input.setWidth(20);
        input.addEventListener(TextInputEvent.ENTER, this::textInputEvent);
        input.addEventListener(TextInputEvent.TEXT_CHANGE, this::textInputEvent);
        input.addEventListener(TextInputEvent.KEY_DOWN, this::textInputEvent);

        loadHistory();

        redraw();
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
        setAlpha(1.0f);
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

    public void addPlayerMessage(int id,
                                 int playerId,
                                 @NotNull String playerName,
                                 int playerColor,
                                 @NotNull String messageText,
                                 @NotNull Color textColor) {

        if (messageText.length() > 70) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < messageText.length(); i += 70) {
                String part = messageText.substring(i, Math.min(i + 70, messageText.length()));
                addPlayerMessage(id, playerId, playerName, playerColor, part, textColor);
            }
            return;
        }

        if (messageText.contains("\n")) {
            messageText.lines().forEach(line ->
                    addPlayerMessage(id, playerId, playerName, playerColor, line, textColor));
            return;
        }


        addMessage(new ChatMessage(id, playerId, playerName, playerColor, messageText, textColor));
        if (id != 0) lastChatMessageId = id;
        redraw();
    }

    public void addMessage(@NotNull String messageText, @NotNull Color textColor) {
        setAlpha(1.0f);
        alphaTime = ALPHA_TIME;
        if (messageText.length() > 100) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < messageText.length(); i += 100) {
                String part = messageText.substring(i, Math.min(i + 100, messageText.length()));
                addMessage(part, textColor);
            }
            return;
        }

        if (messageText.contains("\n")) {
            messageText.lines().forEach(line -> addMessage(line, textColor));
            return;
        }

        addMessage(new ChatMessage(0, messageText, textColor));
        redraw();
    }

    public void print(@NotNull String messageText) {
        log.info(messageText);

        if (messageText.contains("\n")) {
            messageText.lines().forEach(this::print);
        } else {
            addMessage(messageText);
        }
    }

    public void print(@NotNull String messageText, @NotNull Color color) {
        log.info(messageText);

        if (messageText.contains("\n")) {
            messageText.lines().forEach(line -> print(line, color));
        } else {
            addMessage(messageText, color);
        }
    }

    public void addMessage(@NotNull String messageText) {
        addMessage(messageText, Color.WHITE);
    }

    private void addMessage(@NotNull ChatMessage chatMessage) {
        setAlpha(1.0f);
        alphaTime = ALPHA_TIME;
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
        Async.runLater(100, TimeUnit.MILLISECONDS, () -> {
            setAlpha(1.0f);
            alphaTime = ALPHA_TIME;
            add(input);
            dispatchEvent(ChatEvent.builder()
                    .type(ChatEvent.CHAT_INPUT_OPEN)
                    .build());
        });
    }

    public void closeInput() {
        remove(input);

        dispatchEvent(ChatEvent.builder()
                .type(ChatEvent.CHAT_INPUT_CLOSE)
                .build());
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
        if (event instanceof TextInputEvent uiTextInputEvent) {
            switch (event.getType()) {

                case TextInputEvent.TEXT_CHANGE -> {
                    setAlpha(1.0f);
                    alphaTime = ALPHA_TIME;
                    String text = uiTextInputEvent.getText();
                    int length = text.length();
                    if (length > INPUT_MAX_LENGTH) {
                        input.setText(text.substring(0, INPUT_MAX_LENGTH));
                        return;
                    }
                    int w = text.length() * ComponentFont.getBitmapFont().getCharInfo('0').width();
                    input.setWidth(w + 20);
                }

                case TextInputEvent.ENTER -> {
                    String text = uiTextInputEvent.getText();
                    if (!text.isBlank()) {
                        dispatchEvent(ChatEvent.builder()
                                .type(ChatEvent.CHAT_TEXT_ENTER)
                                .text(text)
                                .build());
                        history.add(text);
                        historyIndex = history.size();
                    }
                    input.clear();
                    closeInput();
                }

                case TextInputEvent.KEY_DOWN -> {
                    switch (uiTextInputEvent.getKeyCode()) {
                        case KeyCode.UP -> {
                            if (historyIndex == history.size()) {
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

    public void saveHistory() {
        String toSave = history.stream().reduce("", (s1, s2) -> s1.concat('\n' + s2));
        FileSystemUtils.save("data/chat-history", toSave.getBytes(StandardCharsets.UTF_8));
    }

    private void loadHistory() {
        history.addAll(FileSystemUtils.readString("data/chat-history").lines().toList());
        historyIndex = history.size();
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        alphaTime--;
        if (alphaTime <= 0) {
            setAlpha(0.25f);
            alphaTime = 0;
        }
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2World.init(false, false);

        stage.setBackgroundColor(Color.of(0x223344));

        Holder<Integer> idCounter = new Holder<>(1);

        Chat chat = new Chat();
        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            if (event instanceof ChatEvent chatEvent) {
                String text = chatEvent.getText();
                idCounter.setValue(idCounter.getValue() + 1);
                chat.addPlayerMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, text, Color.WHITE);
            }
        });
        stage.add(chat, 10, 10);

        for (int i = 0; i < 10; i++) {
            idCounter.setValue(idCounter.getValue() + 1);
            chat.addPlayerMessage(idCounter.getValue(), 1, "Ancevt", 0xFFFF00, "Hello, i'm Ancevt" + i, Color.WHITE);
        }

        stage.addEventListener(InputEvent.KEY_DOWN, event -> {
            InputEvent inputEvent = (InputEvent) event;
            switch (inputEvent.getKeyCode()) {
                case KeyCode.PAGE_UP -> {
                    chat.setScroll(chat.getScroll() - 10);
                }

                case KeyCode.PAGE_DOWN -> {
                    chat.setScroll(chat.getScroll() + 10);
                }

                case KeyCode.F6, KeyCode.T -> {
                    if (!chat.isInputOpened()) chat.openInput();
                }
            }
        });
        D2D2.loop();
    }

}
