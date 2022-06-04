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

import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.gameobject.area.AreaCollision;
import com.ancevt.d2d2world.map.Room;
import com.ancevt.d2d2world.world.World;
import com.ancevt.util.args.Args;


public class Editor {

    public static BitmapFont getBitmapFont() {
        return BitmapFont.getDefaultBitmapFont();
    }

    private final World world;
    private final EditorContainer editorContainer;
    private boolean spaceDown;
    private boolean shiftDown;
    private boolean controlDown;
    private boolean altDown;
    private float oldMouseX;
    private float oldMouseY;
    private int currentLayerIndex;
    private final GameObjectEditor gameObjectEditor;
    private boolean enabled;

    public Editor(EditorContainer editorContainer, World world) {
        this.editorContainer = editorContainer;
        this.world = world;

        gameObjectEditor = new GameObjectEditor(this);
        gameObjectEditor.addPlayerActor();

        if (System.getProperties().containsKey("start-position")) {
            Args args = Args.of(System.getProperty("start-position"), ',');
            String roomId = args.next();
            float x = args.next(float.class);
            float y = args.next(float.class);
            getWorld().setSceneryPacked(false);
            getWorld().setRoom(getWorld().getMap().getRoom(roomId));
            gameObjectEditor.addPlayerActor();
            gameObjectEditor.getPlayerActor().setXY(x, y);
            getWorld().getCamera().setXY(x, y);
        }

        setEnabled(true);
    }

    public void key(int keyCode, char keyChar, boolean down) {
        if ((keyCode == KeyCode.TAB || keyChar == 'P') && !isEnabled() && down) {
            setEnabled(true);
            world.setPlaying(false);
            world.getCamera().setBoundsLock(false);
            world.setSceneryPacked(false);
            world.setAreasVisible(true);
            return;
        }

        gameObjectEditor.key(keyCode, keyChar, down);

        if (keyChar == ' ') spaceDown = down;

        if (!isEnabled()) return;

        switch (keyCode) {
            case KeyCode.LEFT_SHIFT, KeyCode.RIGHT_SHIFT -> shiftDown = down;
            case KeyCode.LEFT_CONTROL, KeyCode.RIGHT_CONTROL -> controlDown = down;
            case KeyCode.LEFT_ALT, KeyCode.RIGHT_ALT -> altDown = down;
            case KeyCode.DELETE -> {
                if (down) gameObjectEditor.delete();
            }
            case KeyCode.ENTER -> {
                if (down) gameObjectEditor.enter();
            }
            case KeyCode.BACKSPACE -> {
                if (down) gameObjectEditor.getSelectedGameObject().ifPresent(gameObject -> {

                    if (gameObject instanceof AreaCollision areaCollision) {
                        areaCollision.setFloorOnly(!areaCollision.isFloorOnly());
                    } else {
                        gameObject.setScaleX(gameObject.getScaleX() * -1f);
                    }
                });
            }
            case KeyCode.PAGE_UP -> {
                if (down) prevRoom();
            }
            case KeyCode.PAGE_DOWN -> {
                if (down) nextRoom();
            }
        }


        if (down && keyCode - 48 >= 0 && keyCode - 48 <= 9) {
            int layer = keyCode - 48;
            setCurrentLayerIndex(layer);
            editorContainer.setInfoText("Layer: " + getCurrentLayerIndex());

            if (isControlDown()) {
                gameObjectEditor.moveSelectedToLayer(getCurrentLayerIndex());
            } else {
                gameObjectEditor.unselect();
            }
        }
    }

    public void mouseButton(float x, float y, float worldX, float worldY, boolean down, int mouseButton) {
        gameObjectEditor.getPlayerActor().getController().setB(down);
        if (!isEnabled()) return;

        if (!spaceDown) {
            gameObjectEditor.mouseButton(x, y, worldX, worldY, down, mouseButton);
        }
    }

    public void mouseMove(float x, float y, float worldX, float worldY, boolean drag) {
        gameObjectEditor.getPlayerActor().mouseMove(worldX, worldY);
        if (!isEnabled()) return;

        if (drag && spaceDown) {
            float scale = world.getAbsoluteScaleX();
            world.move((x - oldMouseX) / scale, (y - oldMouseY) / scale);
        }
        if (!spaceDown) {
            gameObjectEditor.mouseMove(x, y, worldX, worldY, drag);
        }

        oldMouseX = x;
        oldMouseY = y;
    }

    public void showRoomInfo() {
        StringBuilder s = new StringBuilder();

        s.append("Rooms:\n");

        for (Room room : getWorld().getMap().getRooms()) {
            String arrow = room == getWorld().getRoom() ? "> " : "  ";
            String startRoom = room == getWorld().getMap().getStartRoom() ? " <== start room" : "";
            s.append(arrow).append(room.getId()).append(startRoom).append('\n');
        }

        getEditorDisplayObject().setInfoText(s.toString());
    }

    private void prevRoom() {
        gameObjectEditor.unselect();

        Room[] rooms = getWorld().getMap().getRooms().toArray(new Room[]{});
        Room currentRoom = getWorld().getRoom();

        for (int i = 1; i < rooms.length; i++) {
            Room room = rooms[i];
            if (room == currentRoom) {
                getWorld().setRoom(rooms[i - 1]);
            }
        }

        showRoomInfo();
    }

    private void nextRoom() {
        gameObjectEditor.unselect();

        Room[] rooms = getWorld().getMap().getRooms().toArray(new Room[]{});
        Room currentRoom = getWorld().getRoom();

        for (int i = 0; i < rooms.length - 1; i++) {
            Room room = rooms[i];
            if (room == currentRoom) {
                getWorld().setRoom(rooms[i + 1]);
            }
        }

        showRoomInfo();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        editorContainer.setVisible(enabled);
        editorContainer.getGrid().setVisible(enabled);
    }

    public boolean isSpaceDown() {
        return spaceDown;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }

    public boolean isControlDown() {
        return controlDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    public int getCurrentLayerIndex() {
        return currentLayerIndex;
    }

    public void setCurrentLayerIndex(int currentLayerIndex) {
        this.currentLayerIndex = currentLayerIndex;
    }

    public EditorContainer getEditorDisplayObject() {
        return editorContainer;
    }

    public World getWorld() {
        return world;
    }

    public GameObjectEditor getGameObjectEditor() {
        return gameObjectEditor;
    }
}
















