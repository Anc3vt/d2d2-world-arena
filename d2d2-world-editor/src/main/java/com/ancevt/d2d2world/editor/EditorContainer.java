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
package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanel;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanelEvent;
import com.ancevt.d2d2world.ui.Grid;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EditorContainer extends Container implements EventListener {

    private final World world;

    private final Editor editor;
    private final Grid grid;

    private final BitmapText infoBitmapText;
    private final BitmapText infoBitmapTextShadow;

    private final List<IDisplayObject> panels;

    public EditorContainer(@NotNull Stage stage, @NotNull World world) {
        this.world = world;
        this.grid = new Grid();

        world.add(grid);

        editor = new Editor(this, world);

        infoBitmapText = new BitmapText(Editor.getBitmapFont());
        infoBitmapTextShadow = new BitmapText(Editor.getBitmapFont());

        infoBitmapText.setColor(Color.WHITE);
        infoBitmapTextShadow.setColor(Color.BLACK);

        add(infoBitmapTextShadow, 4, 12);
        add(infoBitmapText, 5, 12);

        stage.addEventListener(InputEvent.MOUSE_DOWN, this);
        stage.addEventListener(InputEvent.MOUSE_UP, this);
        stage.addEventListener(InputEvent.MOUSE_MOVE, this);
        stage.addEventListener(InputEvent.KEY_DOWN, this);
        stage.addEventListener(InputEvent.KEY_UP, this);
        stage.addEventListener(InputEvent.MOUSE_WHEEL, this);

        add(MapkitToolsPanel.getInstance(), 100, 100);
        MapkitToolsPanel.getInstance().addEventListener(MapkitToolsPanelEvent.MAPKIT_ITEM_SELECT, e -> {
            editor.getGameObjectEditor().setPlacingMapkitItem(((MapkitToolsPanelEvent) e).getMapkitItem());
        });

        panels = new ArrayList<>();
        panels.add(MapkitToolsPanel.getInstance());
    }

    public Grid getGrid() {
        return grid;
    }

    public void setInfoText(String text) {
        infoBitmapText.setText(text);
        infoBitmapTextShadow.setText(text);
    }

    public String getInfoText() {
        return infoBitmapText.getText();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof InputEvent inputEvent) {
            float x = inputEvent.getX();
            float y = inputEvent.getY();

            float wx = world.getAbsoluteX();
            float wy = world.getAbsoluteY();

            float scale = world.getAbsoluteScaleX();

            float worldX = (x - wx) / scale;
            float worldY = (y - wy) / scale;


            switch (event.getType()) {
                case InputEvent.KEY_DOWN -> editor.key(inputEvent.getKeyCode(), inputEvent.getKeyChar(), true);
                case InputEvent.KEY_UP -> editor.key(inputEvent.getKeyCode(), inputEvent.getKeyChar(), false);
                case InputEvent.MOUSE_DOWN -> {
                    if (!isMouseAtPanels(x, y) || world.isPlaying()) editor.mouseButton(x, y, worldX, worldY, true, inputEvent.getMouseButton());
                }
                case InputEvent.MOUSE_UP -> editor.mouseButton(x, y, worldX, worldY, false, inputEvent.getMouseButton());
                case InputEvent.MOUSE_MOVE -> {
                    if (!isMouseAtPanels(x, y) || world.isPlaying())
                        editor.mouseMove(x, y, worldX, worldY, inputEvent.isDrag());
                }
                case InputEvent.MOUSE_WHEEL -> {
                    if(isMouseAtPanels(Mouse.getX(), Mouse.getY())) {
                        MapkitToolsPanel.getInstance().moveY(inputEvent.getDelta() * 100);
                    }
                }
            }
        }
    }

    public boolean isMouseAtPanels(float mouseX, float mouseY) {
        return panels.stream().filter(p->isOnScreen() && p.isVisible()).anyMatch(panel ->
                isMouseAtArea(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight()));
    }

    private boolean isMouseAtArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x < ax + aw && y >= ay - 30 && y < ay + ah;
    }

}





















