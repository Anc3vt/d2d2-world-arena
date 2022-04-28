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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.MouseButton;
import com.ancevt.d2d2world.control.LocalPlayerController;
import com.ancevt.d2d2world.editor.objects.GameObjectLayersMap;
import com.ancevt.d2d2world.editor.objects.SelectArea;
import com.ancevt.d2d2world.editor.objects.SelectRectangle;
import com.ancevt.d2d2world.editor.objects.Selection;
import com.ancevt.d2d2world.editor.swing.JPropertiesEditor;
import com.ancevt.d2d2world.gameobject.ActorEvent;
import com.ancevt.d2d2world.gameobject.GameObjectUtils;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IMovable;
import com.ancevt.d2d2world.gameobject.IRepeatable;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.IRotatable;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.area.Area;
import com.ancevt.d2d2world.gameobject.weapon.AutomaticWeapon;
import com.ancevt.d2d2world.gameobject.weapon.FireWeapon;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.Layer;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class GameObjectEditor {

    private static final int GRID_SIZE = 16;

    private final SelectRectangle selectRectangle;
    private final SelectArea selectArea;
    private final Editor editor;
    private final List<IGameObject> selectedGameObjects;
    private final List<Selection> selections;
    private final List<IGameObject> copyBuffer;
    private final GameObjectLayersMap gameObjectLayersMap;
    private final Set<Layer> lockedLayers;
    private final Cursor cursor;
    private final List<IDisplayObject> collisionRects;
    private MapkitItem placingMapkitItem;
    private Area resizingArea;
    private boolean moving;
    private IRepeatable repeating;
    private boolean snapToGrid;
    private float oldMouseX = 64;
    private float oldMouseY = 64;
    private boolean selecting;
    private MapkitItem lastPlacingMapkitItem;
    private PlayerActor playerActor;
    private boolean collisionVisible;
    private boolean brushMode;
    private int mouseButton;

    public GameObjectEditor(Editor editor) {
        this.editor = editor;

        selectRectangle = new SelectRectangle();
        selectArea = new SelectArea();
        selectedGameObjects = new ArrayList<>();
        selections = new ArrayList<>();
        copyBuffer = new ArrayList<>();
        lockedLayers = new HashSet<>();
        collisionRects = new ArrayList<>();

        setSnapToGrid(false);

        gameObjectLayersMap = new GameObjectLayersMap();
        cursor = new Cursor();
        getWorld().add(cursor);
    }

    public void key(int keyCode, char keyChar, boolean down) {
        if (down) {
            int speed = editor.isShiftDown() ? GRID_SIZE : 1;

            switch (keyCode) {
                case KeyCode.LEFT -> moveSelected(-speed, 0);
                case KeyCode.RIGHT -> moveSelected(speed, 0);
                case KeyCode.UP -> moveSelected(0, -speed);
                case KeyCode.DOWN -> moveSelected(0, speed);
                case KeyCode.ESCAPE -> {
                    setPlacingMapkitItem(null);
                    setBrushMode(false);
                }
                case KeyCode.E -> setPlayerXYToCursor();
                case KeyCode.F9 -> playerActor.addWeapon(AutomaticWeapon.class, 100);
            }

            if (editor.isControlDown()) {
                switch (keyChar) {
                    case 'C' -> copy();
                    case 'V' -> paste();
                    case 'X' -> cut();
                    case 'L' -> toggleLockCurrentLayer();
                    case 'R' -> {
                        if (editor.isControlDown()) {
                            JPropertiesEditor.create(getWorld().getRoom(), text -> {
                                getWorld().setRoom(getWorld().getRoom());
                                editor.showRoomInfo();
                                addPlayerActor();
                            });
                        }
                    }
                    case 'S' -> {
                        resetResettableGameObjects();
                        setInfoText("Saved to " + MapIO.save(getWorld().getMap(), MapIO.getMapFileName()));
                    }

                    case 'Z' -> undo();
                    case 'Y' -> redo();
                    case 'B' -> setBrushMode(!isBrushMode());
                }
            } else {

                switch (keyChar) {
                    case 'C' -> setCollisionsVisible(!isCollisionsVisible());

                    case 'B' -> getWorld().getCamera().setBoundsLock(!getWorld().getCamera().isBoundsLock());

                    /* Ă is TAB */
                    case 'P', 'Ă' -> {
                        editor.setEnabled(false);
                        unselect();
                        getWorld().setPlaying(true);
                        getWorld().getCamera().setBoundsLock(true);
                        getWorld().setSceneryPacked(true);
                        getWorld().setAreasVisible(false);
                    }

                    case 'S' -> {
                        //setSnapToGrid(!isSnapToGrid());
                        snapToGridSelected();
                        //setInfoText("Snap to grid: " + isSnapToGrid());
                    }

                    case 'R' -> editor.showRoomInfo();

                    case 'Q' -> resetResettableGameObjects();

                    case 'V' -> {
                        getWorld().setAreasVisible(!getWorld().isAreasVisible());
                        setInfoText("Area visibility: "
                                + getWorld().isAreasVisible());
                        unselect();
                    }
                    case 'D' -> {
                        if (!getWorld().isPlaying()) setPlacingMapkitItem(lastPlacingMapkitItem);
                    }

                    case 'L' -> setLayerNumbersVisible(true);

                    case 'T' -> {
                        JOptionPane.showConfirmDialog(null, "test");
                    }

                    case 'Ł' -> { // numpad 1
                        getSelectedGameObject().ifPresent(gameObject -> {
                            if (gameObject instanceof IRotatable rotatable) {
                                if (editor.isShiftDown())
                                    rotatable.rotate(-22.5f);
                                else
                                    rotatable.rotate(-1);
                            }
                        });
                    }

                    case 'Ń' -> { // numpad 3
                        getSelectedGameObject().ifPresent(gameObject -> {
                            if (gameObject instanceof IRotatable rotatable) {
                                if (editor.isShiftDown())
                                    rotatable.rotate(+22.5f);
                                else
                                    rotatable.rotate(+1);
                            }
                        });
                    }
                }
            }
            // else if !down
        } else {
            switch (keyChar) {
                case 'L' -> setLayerNumbersVisible(false);
            }
        }

        if (keyCode == KeyCode.LEFT_ALT || keyCode == KeyCode.RIGHT_ALT) {
            sightLayer(down ? editor.getCurrentLayerIndex() : -1);
        }

        putState();
    }

    public void mouseButton(float x, float y, float worldX, float worldY, boolean down, int mouseButton) {
        this.mouseButton = mouseButton;

        if (brushMode) {
            mouseMove(x, y, worldX, worldY, down);
            return;
        }

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
            if (!brushMode) setPlacingMapkitItem(null);
        }

        selecting = true;

        selectRectangle.setUp(worldX, worldY, worldX, worldY);
        selectArea.setXY(selectRectangle);
        getWorld().add(selectArea);

        IGameObject selectedGameObject = getGameObjectUnderPoint(editor.getCurrentLayerIndex(), worldX, worldY);

        if (!isSelected(selectedGameObject) && !editor.isShiftDown()) unselect();

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

        putState();
    }

    public void mouseMove(float x, float y, float worldX, float worldY, boolean drag) {
        if (brushMode && drag) {
            int brushX = (int) worldX;
            int brushY = (int) worldY;

            int needAddX = -brushX & 0xf;
            int needAddY = -brushY & 0xf;

            brushX = brushX - 16 + needAddX;
            brushY = brushY - 16 + needAddY;

            IGameObject gameObject = getGameObjectUnderPoint(editor.getCurrentLayerIndex(), brushX, brushY);
            if (gameObject != null) delete(gameObject);

            if (mouseButton == MouseButton.LEFT && placingMapkitItem != null) {
                createNewGameObject().setXY(brushX, brushY);
                unselect();
            }
            return;
        }

        if (!moving && drag && selecting) {
            selectRectangle.setX2(worldX);
            selectRectangle.setY2(worldY);
            selectArea.setXY(selectRectangle);

            selectGameObjectsInSelectedArea();
        }

        cursor.setXY(worldX - GRID_SIZE / 2, worldY - GRID_SIZE / 2);
        if (isSnapToGrid()) {
            snapToGrid(cursor);
        }

        if (repeating != null && selectedGameObjects.size() == 1 && selectedGameObjects.contains((IGameObject) repeating)) {
            IGameObject gameObject = (IGameObject) repeating;

            int repeatX = (int) ((worldX - gameObject.getX()) / repeating.getOriginalWidth());
            int repeatY = (int) ((worldY - gameObject.getY()) / repeating.getOriginalHeight());

            if (repeatX > 0) repeating.setRepeatX(repeatX);
            if (repeatY > 0) repeating.setRepeatY(repeatY);
        } else if (resizingArea != null && selectedGameObjects.size() == 1 && selectedGameObjects.contains(resizingArea)) {
            float w = worldX - resizingArea.getX();
            float h = worldY - resizingArea.getY();

            if (w >= 0 && h > 0) {
                resizingArea.setSize(worldX - resizingArea.getX(), worldY - resizingArea.getY());
            } else {
                resizingArea.setSize(16f, 16f);
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

    private void setBrushMode(boolean brushMode) {
        this.brushMode = brushMode;
        setInfoText("Brush mode: " + brushMode);
    }

    private boolean isBrushMode() {
        return this.brushMode;
    }

    private void resetResettableGameObjects() {
        getWorld()
                .getMap()
                .getAllGameObjectsFromAllRooms()
                .forEach(
                        gameObject -> {
                            if (gameObject instanceof IResettable resettable) {
                                resettable.reset();
                            }
                        });

    }

    private void putState() {

    }

    private void undo() {

    }

    private void redo() {

    }

    private boolean isCollisionsVisible() {
        return this.collisionVisible;
    }

    private void setCollisionsVisible(boolean visible) {
        this.collisionVisible = visible;
        if (visible) {
            for (int i = 0; i < getWorld().getGameObjectCount(); i++) {
                IGameObject gameObject = getWorld().getGameObject(i);

                if (gameObject instanceof ICollision c) {
                    PlainRect rect = new PlainRect(c.getCollisionWidth(), c.getCollisionHeight(), Color.GREEN) {
                        @Override
                        public void onEachFrame() {
                            setXY(c.getX() + c.getCollisionX(), c.getY() + c.getCollisionY());
                        }
                    };
                    rect.setXY(c.getX() + c.getCollisionX(), c.getY() + c.getCollisionY());
                    rect.setAlpha(0.25f);
                    getWorld().add(rect);

                    collisionRects.add(rect);
                }
            }
        } else {
            while (!collisionRects.isEmpty()) {
                collisionRects.remove(0).removeFromParent();
            }
        }
    }

    private void setPlayerXYToCursor() {
        getWorld().getGameObjects()
                .stream()
                .filter(o -> o instanceof PlayerActor)
                .findAny()
                .ifPresentOrElse(p -> {
                    playerActor = (PlayerActor) p;
                    playerActor.repair();
                    playerActor.setXY(playerActor.getAimX(), playerActor.getAimY());
                }, this::addPlayerActor);
    }

    private void toggleLockCurrentLayer() {
        int layerIndex = editor.getCurrentLayerIndex();
        Layer layer = getWorld().getLayer(layerIndex);
        if (isCurrentLayerLocked()) {
            lockedLayers.remove(layer);
            layer.setAlpha(1);
            setInfoText("Layer unlocked: " + layerIndex);
        } else {
            lockedLayers.add(layer);
            layer.setAlpha(0.5f);
            setInfoText("Layer locked: " + layerIndex);
        }
    }

    private boolean isCurrentLayerLocked() {
        int layerIndex = editor.getCurrentLayerIndex();
        Layer layer = getWorld().getLayer(layerIndex);
        return lockedLayers.contains(layer);
    }

    private void setLayerNumbersVisible(boolean visible) {
        if (visible) {
            LayerNumbers.show(getWorld());
        } else {
            LayerNumbers.hide();
        }

        int currentLayerIndex = editor.getCurrentLayerIndex();

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Layer.LAYER_COUNT; i++) {
            Layer layer = getWorld().getLayer(i);
            if (currentLayerIndex == i) {
                s.append("> ");
            } else {
                s.append("  ");
            }
            s.append("Layer ").append(i);

            if (lockedLayers.contains(layer)) {
                s.append(" locked");
            }
            s.append('\n');

        }
        setInfoText(s.toString());
    }

    private @NotNull IGameObject createNewGameObject() {
        int newGameObjectId = IdGenerator.getInstance().getNewId();
        IGameObject gameObject = getPlacingMapkitItem().createGameObject(newGameObjectId);
        gameObject.setXY(cursor.getX(), cursor.getY());
        gameObject.setName("_" + newGameObjectId + "_" + new Random().nextInt());
        getWorld().addGameObject(gameObject, editor.getCurrentLayerIndex(), true);
        return gameObject;
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
        getSelectedGameObject().ifPresent(gameObject -> {
            if (gameObject instanceof PlayerActor) return;

            copyBuffer.clear();
            copyBuffer.addAll(selectedGameObjects);

            gameObjectLayersMap.clear();

            copyBuffer.forEach(gameObjectToCopy ->
                    gameObjectLayersMap.put(gameObjectToCopy.getGameObjectId(), GameObjectUtils.getLayerIndex(gameObjectToCopy))
            );

            setInfoText("Copied " + copyBuffer.size() + " objects");
        });
    }

    private void paste() {
        unselect();
        copyBuffer.forEach(gameObject -> {
            IGameObject copy = GameObjectUtils.copy(gameObject, IdGenerator.getInstance().getNewId());
            copy.setName(copy.getName() + "_" + copy.getGameObjectId());
            getWorld().addGameObject(copy, gameObjectLayersMap.get(gameObject.getGameObjectId()), true);
            select(copy);
        });
    }

    private void cut() {
        copy();
        delete();
    }

    public void delete() {
        getSelectedGameObject().ifPresent(
                gameObject -> {
                    if (gameObject instanceof PlayerActor) return;

                    int count = selectedGameObjects.size();
                    if (count == 0) return;

                    clearSelections();

                    for (IGameObject o : selectedGameObjects) delete(o);
                    selectedGameObjects.clear();

                    updateSelecting();

                    if (count == 1) {
                        setInfoText("Deleted " + gameObject);
                    } else {
                        setInfoText("Deleted " + count + " objects");
                    }

                });
    }

    public void delete(IGameObject gameObject) {
        getWorld().removeGameObject(gameObject, true);
        IdGenerator.getInstance().removeId(gameObject.getGameObjectId());
    }


    private World getWorld() {
        return editor.getWorld();
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
        int currentLayerIndex = editor.getCurrentLayerIndex();

        int count = getWorld().getGameObjectCount();
        for (int i = count - 1; i >= 0; i--) {
            IGameObject gameObject = getWorld().getGameObject(i);
            if (!gameObject.isVisible()) continue;

            Layer worldLayer = (Layer) gameObject.getParent();

            if ((worldLayer != null &&
                    (currentLayerIndex == worldLayer.getIndex() || editor.isControlDown()))) {

                if (hitTest(selectArea, gameObject)) {
                    select(gameObject);
                } else {
                    unselect(gameObject);
                }
            }
        }
    }

    private static boolean hitTest(float x, float y, @NotNull IDisplayObject o) {
        float ox = o.getX(), oy = o.getY(), ow = o.getWidth(), oh = o.getHeight();
        if (o instanceof ICollision c) {
            ox += c.getCollisionX();
            oy += c.getCollisionY();
        }

        return x >= ox &&
                x < ox + ow &&
                y >= oy &&
                y < oy + oh;
    }

    private static boolean hitTest(@NotNull IDisplayObject o1, IDisplayObject o2) {

        float x1 = o1.getX(), y1 = o1.getY(), w1 = o1.getWidth(), h1 = o1.getHeight();

        if (o1 instanceof ICollision c) {
            x1 += c.getCollisionX();
            y1 += c.getCollisionY();
        }

        float x2 = o2.getX(), y2 = o2.getY(), w2 = o2.getWidth(), h2 = o2.getHeight();

        if (o2 instanceof ICollision c) {
            x2 += c.getCollisionX();
            y2 += c.getCollisionY();
        }

        return x1 + w1 > x2 &&
                x1 < x2 + w2 &&
                y1 + h1 > y2 &&
                y1 < y2 + h2;
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

    private void snapToGrid(@NotNull IDisplayObject displayObject) {
        int x = (int) displayObject.getX();
        int y = (int) displayObject.getY();

        while (x % GRID_SIZE != 0) x--;
        while (y % GRID_SIZE != 0) y--;

        displayObject.setXY(x, y);
        if (displayObject instanceof IMovable m) {
            m.setStartXY(x, y);
            m.reset();
        }
    }

    public boolean isSomethingSelected() {
        return !selectedGameObjects.isEmpty();
    }

    public Optional<IGameObject> getSelectedGameObject() {
        if (selectedGameObjects.size() > 0) return Optional.of(selectedGameObjects.get(0));

        return Optional.empty();
    }

    public final void select(IGameObject o) {
        if (!selectedGameObjects.contains(o) && !isGameObjectInLockedLayer(o)) {

            selectedGameObjects.add(o);
        }
        updateSelecting();

        updateSelectedGameObjectsTextInfo();
    }

    private boolean isGameObjectInLockedLayer(IGameObject o) {
        return lockedLayers.contains(getWorld().getLayerByGameObject(o));
    }

    private void updateSelectedGameObjectsTextInfo() {
        if (selectedGameObjects.isEmpty()) {
            setInfoText("");
        } else if (selectedGameObjects.size() == 1) {
            IGameObject gameObject = selectedGameObjects.get(0);
            setInfoText("Layer: " + getWorld().getLayerByGameObject(gameObject).getIndex() + " " + selectedGameObjects.get(0));
        } else {
            setInfoText("Selected " + selectedGameObjects.size() + " game objects");
        }
    }

    @Contract(pure = true)
    private @Nullable Selection getSelectionByGameObject(final IGameObject gameObject) {
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

            if (gameObject instanceof ICollision c) {
                selection.move(c.getCollisionX(), c.getCollisionY());
            }

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
            if (isGameObjectInLockedLayer(gameObject)) continue;
            if (gameObject.isVisible() && gameObject.hasParent() && gameObject.getParent() instanceof Layer layer) {
                if (editor.isControlDown() || layerIndex == layer.getIndex()) {
                    if (hitTest(x, y, gameObject)) return gameObject;
                }
            }
        }

        return null;
    }

    public void setInfoText(Object o) {
        editor.getEditorDisplayObject().setInfoText("" + o);
    }

    public String getInfoText() {
        return editor.getEditorDisplayObject().getInfoText();
    }

    public void appendInfoText(Object o) {
        setInfoText(getInfoText() + o);
    }

    public void moveSelected(float x, float y) {
        if (isCollisionsVisible()) {
            getSelectedGameObject().ifPresent(gameObject -> {
                if (gameObject instanceof ICollision collision) {
                    if (editor.isControlDown()) {
                        collision.setCollisionWidth(collision.getCollisionWidth() + x);
                        collision.setCollisionHeight(collision.getCollisionHeight() + y);
                    } else {
                        collision.setCollisionX(collision.getCollisionX() + x);
                        collision.setCollisionY(collision.getCollisionY() + y);
                    }
                    setCollisionsVisible(false);
                    setCollisionsVisible(true);
                }
            });
        } else {
            for (IGameObject gameObject : selectedGameObjects) {
                gameObject.move(x, y);

                if (gameObject instanceof IMovable m)
                    m.setStartXY(gameObject.getX(), gameObject.getY());
            }
        }
    }

    public void enter() {
        if (isSomethingSelected() && selections.size() == 1) {
            JPropertiesEditor.create(getSelectedGameObject().orElseThrow());
        }
    }

    public void moveSelectedToLayer(int layerIndex) {
        selectedGameObjects.forEach(gameObject -> {
            getWorld().removeGameObject(gameObject, true);
            getWorld().addGameObject(gameObject, layerIndex, true);
        });
    }

    public PlayerActor getPlayerActor() {
        return playerActor;
    }

    public void addPlayerActor() {
        MapkitItem playerActorMapkitItem = MapkitManager.getInstance()
                .getMapkit(BuiltInMapkit.NAME)
                .getItemById("character_blake");

        playerActor = (PlayerActor) playerActorMapkitItem.createGameObject(-1);
        playerActor.setXY(64, 64);

        playerActor.setName("lpa");
        playerActor.setLocalAim(true);
        playerActor.setLocalPlayerActor(true);
        getWorld().addGameObject(playerActor, 5, false);
        LocalPlayerController localPlayerController = new LocalPlayerController();
        localPlayerController.setEnabled(true);
        playerActor.setController(localPlayerController);

        playerActor.addEventListener(ActorEvent.ACTOR_ENTER_ROOM, event -> getWorld().roomSwitchOverlayStartOut());

        getWorld().getCamera().setAttachedTo(playerActor);

        Root root = D2D2.getStage().getRoot();

        root.removeEventListener(hashCode() + InputEvent.KEY_DOWN);
        root.addEventListener(hashCode() + InputEvent.KEY_DOWN, InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            localPlayerController.key(e.getKeyCode(), e.getKeyChar(), true);
            if (e.getKeyCode() == KeyCode.F) playerActor.nextWeapon();
        });

        root.removeEventListener(hashCode() + InputEvent.KEY_UP);
        root.addEventListener(hashCode() + InputEvent.KEY_UP, InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            localPlayerController.key(e.getKeyCode(), e.getKeyChar(), false);
        });

        root.removeEventListener(hashCode() + InputEvent.MOUSE_WHEEL);
        root.addEventListener(hashCode() + InputEvent.MOUSE_WHEEL, InputEvent.MOUSE_WHEEL, event -> {
            var e = (InputEvent) event;
            if (e.getDelta() > 0) {
                playerActor.nextWeapon();
            } else {
                playerActor.prevWeapon();
            }
        });

        playerActor.addWeapon(AutomaticWeapon.class, 100);
        playerActor.addWeapon(FireWeapon.class, 100);

        //DebugActorCreator.createTestPlayerActor(playerActor, getWorld());
    }
}
