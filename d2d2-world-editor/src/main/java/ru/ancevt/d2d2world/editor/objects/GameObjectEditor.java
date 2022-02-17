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
package ru.ancevt.d2d2world.editor.objects;

import ru.ancevt.d2d2world.editor.Cursor;
import ru.ancevt.d2d2world.editor.swing.JPropertiesEditor;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2world.editor.Editor;
import ru.ancevt.d2d2world.gameobject.GameObjectUtils;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.IMovable;
import ru.ancevt.d2d2world.gameobject.IRepeatable;
import ru.ancevt.d2d2world.gameobject.area.Area;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;
import ru.ancevt.d2d2world.world.Layer;
import ru.ancevt.d2d2world.world.World;

import java.util.ArrayList;
import java.util.List;

public class GameObjectEditor {

    private static final int GRID_SIZE = 16;

    private final SelectRectangle selectRectangle;
    private final SelectArea selectArea;
    private final Editor gameObjectEditor;
    private final List<IGameObject> selectedGameObjects;
    private final List<Selection> selections;
    private final List<IGameObject> copyBuffer;
    private final GameObjectLayersMap gameObjectLayersMap;
    private MapkitItem placingMapkitItem;
    private Area resizingArea;
    private boolean moving;
    private IRepeatable repeating;
    private boolean snapToGrid;
    private float oldMouseX;
    private float oldMouseY;
    private boolean selecting;
    private final Cursor cursor;
    private MapkitItem lastPlacingMapkitItem;

    public GameObjectEditor(Editor gameObjectEditor) {
        this.gameObjectEditor = gameObjectEditor;

        selectRectangle = new SelectRectangle();
        selectArea = new SelectArea();
        selectedGameObjects = new ArrayList<>();
        selections = new ArrayList<>();
        copyBuffer = new ArrayList<>();

        setSnapToGrid(true);

        gameObjectLayersMap = new GameObjectLayersMap();
        cursor = new Cursor();
        getWorld().add(cursor);
    }

    public void mouseButton(float x, float y, float worldX, float worldY, boolean down) {
        if (!down) {
            selectArea.removeFromParent();

            if (moving && snapToGrid) {
                snapToGridSelected();
            } else {
                selectedGameObjects.forEach(gameObject -> gameObject.setXY(
                        (float) Math.floor(gameObject.getX()),
                        (float) Math.floor(gameObject.getY())
                ));
            }

            repeating = null;
            resizingArea = null;
            moving = false;
            selecting = false;
            return;
        }

        if (getPlacingMapkitItem() != null) {
            createNewGameObject();
            setPlacingMapkitItem(null);
        }

        selecting = true;

        selectRectangle.setUp(worldX, worldY, worldX, worldY);
        selectArea.setXY(selectRectangle);
        getWorld().add(selectArea);

        IGameObject selectedGameObject = getGameObjectUnderPoint(gameObjectEditor.getCurrentLayerIndex(), worldX, worldY);

        if (!isSelected(selectedGameObject) && !gameObjectEditor.isShiftDown()) unselect();

        if (selectedGameObject != null) {
            select(selectedGameObject);
            moving = true;

            if (worldX > selectedGameObject.getX() + selectedGameObject.getWidth() - 8 &&
                    worldY > selectedGameObject.getY() + selectedGameObject.getHeight() - 8 &&
                    worldX < selectedGameObject.getX() + selectedGameObject.getWidth() &&
                    worldY < selectedGameObject.getY() + selectedGameObject.getHeight()) {

                if (selectedGameObject instanceof IRepeatable r)
                    repeating = r;
                else if (selectedGameObject instanceof Area)
                    resizingArea = (Area) selectedGameObject;
            }
        } else {
            moving = false;
            unselect();
        }
    }

    private void createNewGameObject() {
        int newGameObjectId = getWorld().getMap().getNextFreeGameObjectId();
        IGameObject gameObject = getPlacingMapkitItem().createGameObject(newGameObjectId);
        gameObject.setXY(cursor.getX(), cursor.getY());
        gameObject.setName("_" + newGameObjectId);
        getWorld().addGameObject(gameObject, gameObjectEditor.getCurrentLayerIndex(), true);
    }

    public void mouseMove(float x, float y, float worldX, float worldY, boolean drag) {
        if (!moving && drag && selecting) {
            selectRectangle.setX2(worldX);
            selectRectangle.setY2(worldY);
            selectArea.setXY(selectRectangle);

            selectGameObjectsInSelectedArea();
        }

        cursor.setXY(worldX, worldY);
        if (isSnapToGrid()) {
            snapToGrid(cursor);
        }

        if (repeating != null && selectedGameObjects.size() == 1
                && selectedGameObjects.contains((IGameObject) repeating)) {

            IGameObject gameObject = (IGameObject) repeating;

            int repeatX = (int) ((worldX - gameObject.getX()) / repeating.getOriginalWidth());
            int repeatY = (int) ((worldY - gameObject.getY()) / repeating.getOriginalHeight());

            if (repeatX > 0) repeating.setRepeatX(repeatX);
            if (repeatY > 0) repeating.setRepeatY(repeatY);
        } else if (resizingArea != null && selectedGameObjects.size() == 1 && selectedGameObjects.contains(resizingArea)) {
            float w = worldX - resizingArea.getX();
            float h = worldY - resizingArea.getY();

            if (w > 0 && h > 0) {
                resizingArea.setSize(worldX - resizingArea.getX(), worldY - resizingArea.getY());
            }
        } else if (!selectedGameObjects.isEmpty() && moving) {
            float scale = getWorld().getAbsoluteScaleX();
            moveSelected(
                    (x - oldMouseX) / scale,
                    (y - oldMouseY) / scale
            );
        }

        oldMouseX = x;
        oldMouseY = y;
    }

    public void key(int keyCode, char keyChar, boolean down) {
        if (down) {
            int speed = gameObjectEditor.isShiftDown() ? GRID_SIZE : 1;

            switch (keyCode) {
                case KeyCode.LEFT -> moveSelected(-speed, 0);
                case KeyCode.RIGHT -> moveSelected(speed, 0);
                case KeyCode.UP -> moveSelected(0, -speed);
                case KeyCode.DOWN -> moveSelected(0, speed);
            }

            if (gameObjectEditor.isControlDown()) {
                switch (keyChar) {
                    case 'C' -> copy();
                    case 'V' -> paste();
                    case 'X' -> cut();
                }
            } else

                switch (keyChar) {
                    case 'Q' -> {
                        getWorld().resetGameObjects();
                    }

                    case 'S' -> {
                        setSnapToGrid(!isSnapToGrid());
                        infoText("Snap to grid: " + isSnapToGrid());
                    }
                    case 'V' -> {
                        getWorld().setAreasVisible(!getWorld().isAreasVisible());
                        infoText("Area visibility: "
                                + getWorld().isAreasVisible());
                        unselect();
                    }
                    case 'D' -> {
                        setPlacingMapkitItem(lastPlacingMapkitItem);
                    }
                }
        }

        if (keyCode == KeyCode.LEFT_ALT || keyCode == KeyCode.RIGHT_ALT) {
            sightLayer(down ? gameObjectEditor.getCurrentLayerIndex() : -1);
        }
    }

    public void setPlacingMapkitItem(MapkitItem placingMapkitItem) {
        this.placingMapkitItem = placingMapkitItem;
        cursor.setMapKitItem(placingMapkitItem);
        if (placingMapkitItem != null) {
            lastPlacingMapkitItem = placingMapkitItem;
        }
    }

    public MapkitItem getPlacingMapkitItem() {
        return placingMapkitItem;
    }

    private void copy() {
        copyBuffer.clear();
        copyBuffer.addAll(selectedGameObjects);

        gameObjectLayersMap.clear();

        copyBuffer.forEach(gameObject ->
                gameObjectLayersMap.put(gameObject.getGameObjectId(), GameObjectUtils.getLayerIndex(gameObject))
        );

        infoText("Copied " + copyBuffer.size() + " objects");
    }

    private void paste() {
        unselect();

        copyBuffer.forEach(gameObject -> {
            IGameObject copy = GameObjectUtils.copy(gameObject, newGameObjectId());
            copy.move(1f, 1f);

            getWorld().addGameObject(
                    copy, gameObjectLayersMap.get(gameObject.getGameObjectId()), true
            );

            select(copy);
        });
    }

    private void cut() {
        copy();
        delete();
    }

    private World getWorld() {
        return gameObjectEditor.getWorld();
    }

    private void infoText(String text) {
        gameObjectEditor.getEditorDisplayObject().setInfoText(text);
    }

    private int newGameObjectId() {
        return gameObjectEditor.getWorld().getMap().getNextFreeGameObjectId();
    }

    private void snapToGridSelected() {
        for (IGameObject gameObject : selectedGameObjects) {
            snapToGrid(gameObject);

            if (gameObject instanceof Area a) {
                while ((int) a.getWidth() % GRID_SIZE != 0) a.setWidth(a.getWidth() + 1);
                while ((int) a.getHeight() % GRID_SIZE != 0) a.setHeight(a.getHeight() + 1);
            }
        }
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    private void selectGameObjectsInSelectedArea() {
        float sX = selectArea.getX();
        float sY = selectArea.getY();
        float sW = selectArea.getWidth();
        float sH = selectArea.getHeight();

        int currentLayerIndex = gameObjectEditor.getCurrentLayerIndex();

        int count = getWorld().getGameObjectCount();
        for (int i = count - 1; i >= 0; i--) {
            IGameObject gameObject = getWorld().getGameObject(i);
            if (!gameObject.isVisible()) continue;

            Layer worldLayer = (Layer) gameObject.getParent();

            if ((worldLayer != null &&
                    (currentLayerIndex == worldLayer.getIndex() || gameObjectEditor.isControlDown()))) {

                float cX = gameObject.getX();
                float cY = gameObject.getY();
                float cW = gameObject.getWidth();
                float cH = gameObject.getHeight();

                if (
                        sX + sW > cX &&
                                sX < cX + cW &&
                                sY + sH > cY &&
                                sY < cY + cH
                ) {
                    select(gameObject);
                } else {
                    unselect(gameObject);
                }
            }
        }
    }

    private void sightLayer(int layerIndex) {
        World world = getWorld();

        for (int i = 0; i < Layer.LAYER_COUNT; i++) {
            Layer layer = world.getLayer(i);

            layer.setAlpha(0.125f);

            if (layerIndex == -1 || i == layerIndex) {
                layer.setAlpha(1.0f);
            }
        }
    }

    private void snapToGrid(IDisplayObject displayObject) {
        int x = (int) displayObject.getX();
        int y = (int) displayObject.getY();

        while (x % GRID_SIZE != 0) x--;
        while (y % GRID_SIZE != 0) y--;

        displayObject.setXY(x, y);
    }

    public boolean isSomethingSelected() {
        return !selectedGameObjects.isEmpty();
    }

    public IGameObject getSelectedGameObject() {
        if (selectedGameObjects.size() > 0) return selectedGameObjects.get(0);

        throw new IllegalStateException("no selected game objects");
    }

    public final void select(IGameObject gameObject) {
        if (!selectedGameObjects.contains(gameObject)) selectedGameObjects.add(gameObject);
        updateSelecting();

        updateSelectedGameObjectsTextInfo();
    }

    private void updateSelectedGameObjectsTextInfo() {
        if (selectedGameObjects.isEmpty()) {
            infoText("");
        } else if (selectedGameObjects.size() == 1) {
            IGameObject gameObject = selectedGameObjects.get(0);
            infoText("Layer: " + getWorld().getLayerByGameObject(gameObject).getIndex() + " " + selectedGameObjects.get(0));
        } else {
            infoText("Selected " + selectedGameObjects.size() + " game objects");
        }
    }

    private Selection getSelectionByGameObject(final IGameObject gameObject) {
        for (final Selection selection : selections)
            if (selection.getGameObject() == gameObject)
                return selection;

        return null;
    }

    public synchronized final void unselect() {
        while (!selectedGameObjects.isEmpty()) {
            unselect(selectedGameObjects.get(0));
        }
    }

    public final void unselect(final IGameObject gameObject) {
        selectedGameObjects.remove(gameObject);
        final Selection selection = getSelectionByGameObject(gameObject);
        if (selection == null) return;
        if (selection.hasParent())
            selection.getParent().remove(selection);

        selections.remove(selection);
    }

    private void clearSelections() {
        while (!selections.isEmpty()) {
            final Selection selection = selections.remove(0);
            if (selection.hasParent()) selection.removeFromParent();
        }
    }

    private void updateSelecting() {
        clearSelections();

        for (final IGameObject gameObject : selectedGameObjects) {
            final Selection selection = new Selection(gameObject);
            selection.setXY(gameObject.getX(), gameObject.getY());
            selections.add(selection);
            getWorld().add(selection);
        }
    }

    public boolean isSelected(IGameObject gameObject) {
        return selectedGameObjects.contains(gameObject);
    }

    public IGameObject getGameObjectUnderPoint(int layerIndex, float x, float y) {
        for (int i = getWorld().getGameObjectCount() - 1; i >= 0; i--) {
            IGameObject gameObject = getWorld().getGameObject(i);
            if (gameObject.isVisible() && gameObject.hasParent() && gameObject.getParent() instanceof Layer layer) {
                if (gameObjectEditor.isControlDown() || layerIndex == layer.getIndex()) {
                    if (x >= gameObject.getX() &&
                            x < gameObject.getX() + gameObject.getWidth() &&
                            y >= gameObject.getY() &&
                            y < gameObject.getY() + gameObject.getHeight()) {

                        return gameObject;
                    }
                }
            }
        }

        return null;
    }

    public void moveSelected(float x, float y) {
        for (IGameObject gameObject : selectedGameObjects) {
            gameObject.move(x, y);

            if (gameObject instanceof IMovable m)
                m.setStartXY(gameObject.getX(), gameObject.getY());
        }
    }

    public void delete() {
        int count = selectedGameObjects.size();
        if (count == 0) return;

        IGameObject gameObject = getSelectedGameObject();

        clearSelections();

        for (IGameObject o : selectedGameObjects)
            getWorld().removeGameObject(o, true);

        selectedGameObjects.clear();

        updateSelecting();

        if (count == 1) {
            infoText("Deleted " + gameObject);
        } else {
            infoText("Deleted " + count + " objects");
        }
    }

    public void enter() {
        if (isSomethingSelected() && selections.size() == 1) {
            JPropertiesEditor.create(getWorld(), getSelectedGameObject());
        }
    }

    public void moveSelectedToLayer(int layerIndex) {
        selectedGameObjects.forEach(gameObject -> {
            getWorld().removeGameObject(gameObject, true);
            getWorld().addGameObject(gameObject, layerIndex, true);
        });
    }
}
