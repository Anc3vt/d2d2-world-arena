/*
 *   D2D2 World Editor
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
            Args args = new Args(System.getProperty("start-position"), ',');
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
















