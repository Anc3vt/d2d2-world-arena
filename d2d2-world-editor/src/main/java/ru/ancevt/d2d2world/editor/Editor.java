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
package ru.ancevt.d2d2world.editor;

import ru.ancevt.d2d2world.editor.objects.GameObjectEditor;
import ru.ancevt.d2d2world.editor.swing.JPropertiesEditor;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2world.gameobject.ICollision;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.map.Room;
import ru.ancevt.d2d2world.world.World;

import java.util.ArrayList;
import java.util.List;


public class Editor {

    public static BitmapFont getBitmapFont() {
        return BitmapFont.getDefaultBitmapFont();
    }

    private final World world;
    private final EditorDisplayObject editorDisplayObject;
    private boolean spaceDown;
    private boolean shiftDown;
    private boolean controlDown;
    private boolean altDown;
    private float oldMouseX;
    private float oldMouseY;
    private int currentLayerIndex;
    private final GameObjectEditor gameObjectEditor;
    private boolean enabled;

    public Editor(EditorDisplayObject editorDisplayObject, World world) {
        this.editorDisplayObject = editorDisplayObject;
        this.world = world;

        gameObjectEditor = new GameObjectEditor(this);

        setEnabled(true);
    }

    public void mouseButton(float x, float y, float worldX, float worldY, boolean down) {
        if (!isEnabled()) return;

        if (!spaceDown) {
            gameObjectEditor.mouseButton(x, y, worldX, worldY, down);
        }
    }

    public void mouseMove(float x, float y, float worldX, float worldY, boolean drag) {
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

    public void key(int keyCode, char keyChar, boolean down) {
        if (keyChar == 'P' && !isEnabled() && down) {
            setEnabled(true);
            world.setPlaying(false);
            world.getCamera().setBoundsLock(false);
            world.setSceneryPacked(false);
            return;
        }

        if (!isEnabled()) return;

        gameObjectEditor.key(keyCode, keyChar, down);

        switch (keyChar) {
            case 'C' -> {
                setCollisionsVisible(down);
            }

            case 'P' -> {
                if (down) {
                    setEnabled(false);
                    world.setPlaying(true);
                    world.getCamera().setBoundsLock(true);
                    world.setSceneryPacked(true);
                }
            }

            case 'S' -> {
                if (down && isControlDown()) {
                    getEditorDisplayObject().setInfoText("Saved to " + MapSaver.save(getWorld().getMap(), MapSaver.mapFileName));
                }
            }

            case 'R' -> {
                if (down) {
                    if (isControlDown()) {
                        JPropertiesEditor.create(getWorld().getRoom(), text -> {
                            world.setRoom(world.getRoom());
                            showRoomInfo();
                        });
                    } else {
                        showRoomInfo();
                    }
                }

            }

            case 'L' -> {
                setLayerNumbersVisible(down);
                editorDisplayObject.setInfoText("Layer: " + getCurrentLayerIndex());
            }

            case ' ' -> {
                spaceDown = down;
            }
        }

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
                if (down) gameObjectEditor.getSelectedGameObject().setScaleX(
                        gameObjectEditor.getSelectedGameObject().getScaleX() * -1f
                );
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
            editorDisplayObject.setInfoText("Layer: " + getCurrentLayerIndex());

            if (isControlDown()) {
                gameObjectEditor.moveSelectedToLayer(getCurrentLayerIndex());
            } else {
                gameObjectEditor.unselect();
            }
        }
    }

    public void setLayerNumbersVisible(boolean visible) {
        if (visible) {
            LayerNumbers.show(getWorld());
        } else {
            LayerNumbers.hide();
        }
    }

    private final List<IDisplayObject> collisionRects = new ArrayList<>();

    private void setCollisionsVisible(boolean visible) {
        if (visible) {
            for (int i = 0; i < world.getGameObjectCount(); i++) {
                IGameObject gameObject = world.getGameObject(i);

                if (gameObject instanceof ICollision c) {
                    PlainRect rect = new PlainRect(c.getCollisionWidth(), c.getCollisionHeight(), Color.GREEN) {
                        @Override
                        public void onEachFrame() {
                            setXY(c.getX() + c.getCollisionX(), c.getY() + c.getCollisionY());
                        }
                    };
                    rect.setXY(c.getX() + c.getCollisionX(), c.getY() + c.getCollisionY());
                    rect.setAlpha(0.25f);
                    world.add(rect);

                    collisionRects.add(rect);
                }
            }
        } else {
            while (!collisionRects.isEmpty()) {
                collisionRects.remove(0).removeFromParent();
            }
        }
    }

    private void showRoomInfo() {
        StringBuilder s = new StringBuilder();

        s.append("Rooms:\n");

        Room[] rooms = getWorld().getMap().getRooms();
        for (Room room : rooms) {
            String arrow = room == getWorld().getRoom() ? "> " : "  ";
            String startRoom = room == getWorld().getMap().getStartRoom() ? " <== start room" : "";
            s.append(arrow).append(room.getId()).append(startRoom).append('\n');
        }

        getEditorDisplayObject().setInfoText(s.toString());
    }

    private void prevRoom() {
        gameObjectEditor.unselect();

        Room[] rooms = getWorld().getMap().getRooms();
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

        Room[] rooms = getWorld().getMap().getRooms();
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

        editorDisplayObject.setVisible(enabled);
        editorDisplayObject.getGrid().setVisible(enabled);
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

    public EditorDisplayObject getEditorDisplayObject() {
        return editorDisplayObject;
    }

    public World getWorld() {
        return world;
    }

    public GameObjectEditor getGameObjectEditor() {
        return gameObjectEditor;
    }
}
















