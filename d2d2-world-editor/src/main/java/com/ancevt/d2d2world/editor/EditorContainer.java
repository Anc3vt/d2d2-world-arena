/*
 *   D2D2 World Editor
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
package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanel;
import com.ancevt.d2d2world.editor.panels.MapkitToolsPanelEvent;
import com.ancevt.d2d2world.world.World;

import java.util.ArrayList;
import java.util.List;

public class EditorContainer extends DisplayObjectContainer implements EventListener {

    private final World world;

    private final Editor editor;
    private final Grid grid;

    private final BitmapText infoBitmapText;
    private final BitmapText infoBitmapTextShadow;

    private final List<IDisplayObject> panels;

    public EditorContainer(Root root, World world) {
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


        root.addEventListener(InputEvent.MOUSE_DOWN, this);
        root.addEventListener(InputEvent.MOUSE_UP, this);
        root.addEventListener(InputEvent.MOUSE_MOVE, this);
        root.addEventListener(InputEvent.KEY_DOWN, this);
        root.addEventListener(InputEvent.KEY_UP, this);

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
                    if (!isMouseAtPanels(x, y)) editor.mouseButton(x, y, worldX, worldY, true);
                }
                case InputEvent.MOUSE_UP -> editor.mouseButton(x, y, worldX, worldY, false);
                case InputEvent.MOUSE_MOVE -> {
                    if (!isMouseAtPanels(x, y))
                        editor.mouseMove(x, y, worldX, worldY, inputEvent.isDrag());
                }

            }
        }
    }

    private boolean isMouseAtPanels(float mouseX, float mouseY) {
        return panels.stream().anyMatch(panel ->
                isMouseAtArea(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight()));
    }

    private boolean isMouseAtArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x < ax + aw && y >= ay - 30 && y < ay + ah;
    }

}





















