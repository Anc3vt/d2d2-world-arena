/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.desktop.scene.charselect;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.touch.TouchButton;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;
import com.ancevt.d2d2world.desktop.settings.DesktopConfig;
import com.ancevt.d2d2world.desktop.ui.UiText;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CharSelectScene extends DisplayObjectContainer {

    private final PlainRect bg;
    private final Set<CharSelectItem> charSelectItems;

    public CharSelectScene() {
        bg = new PlainRect(Color.of(0x000011));
        add(bg);
        charSelectItems = new HashSet<>();

        UiText uiLabel = new UiText("Select a character:");
        add(uiLabel, 10, 14);

        addEventListener(Event.ADD_TO_STAGE, Event.ADD_TO_STAGE, this::this_addToStage);
        setScale(1.5f, 1.5f);
    }

    public void dispose() {
        for (CharSelectItem charSelectItem : charSelectItems) {
            charSelectItem.dispose();
        }
    }

    private void this_addToStage(Event event) {
        removeEventListener(Event.ADD_TO_STAGE);

        final float sw = getStage().getStageWidth();
        final float sh = getStage().getStageHeight();

        bg.setSize(sw, sh);

        final float STEP_X = 64f;
        final float STEP_Y = 64f;

        float x = STEP_X;
        float y = STEP_Y + 16;

        List<MapkitItem> items = BuiltInMapkit.getInstance().getCharacterMapkitItems();
        for (MapkitItem mki : items) {
            CharSelectItem charSelectItem = new CharSelectItem(mki, this);
            add(charSelectItem, x, y);
            charSelectItems.add(charSelectItem);

            x += STEP_X;

            if (x >= sw) {
                x = STEP_X;
                y += STEP_Y;
            }
        }

        String debugCharacterMapkitItem = DesktopConfig.CONFIG.getString(DesktopConfig.DEBUG_CHARACTER);

        if (!debugCharacterMapkitItem.isEmpty()) {
            MapkitItem mapkitItem = BuiltInMapkit.getInstance().getItemById(debugCharacterMapkitItem);
            dispatchEvent(CharSelectSceneEvent.builder()
                    .type(CharSelectSceneEvent.CHARACTER_SELECT)
                    .mapkitItem(mapkitItem).build()
            );
            dispose();
            removeFromParent();
        }
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class CharSelectSceneEvent extends Event<CharSelectScene> {

        public static final String CHARACTER_SELECT = "characterSelect";

        private final MapkitItem mapkitItem;
    }

    private static class CharSelectItem extends DisplayObjectContainer {

        private final MapkitItem mapkitItem;
        private final CharSelectScene charSelectScene;
        private final BorderedRect borderedRect;
        private final TouchButton touchButton;
        private final PlayerActor playerActor;
        private final Sprite decorDoor;

        public CharSelectItem(@NotNull MapkitItem mapkitItem, CharSelectScene charSelectScene) {
            this.mapkitItem = mapkitItem;
            this.charSelectScene = charSelectScene;

            decorDoor = new Sprite(D2D2WorldArenaDesktopAssets.getCharSelectDoorTexture());

            playerActor = (PlayerActor) mapkitItem.createGameObject(0);
            playerActor.setAnimation(AnimationKey.WALK, true);
            playerActor.setHealthBarVisible(false);

            borderedRect = new BorderedRect(mapkitItem.getTexture().width(), mapkitItem.getTexture().height(), Color.of(0x111111), Color.BLACK);

            addEventListener(Event.ADD_TO_STAGE, Event.ADD_TO_STAGE, this::this_addToStage);

            touchButton = new TouchButton(true);
        }

        private void this_addToStage(Event event) {
            removeEventListener(Event.ADD_TO_STAGE);

            final float w = mapkitItem.getTexture().width();
            final float h = mapkitItem.getTexture().height();

            add(borderedRect, -w / 2, -h / 2);

            decorDoor.setVisible(false);
            add(decorDoor, 8, -h / 2);

            playerActor.setWeaponVisible(false);
            add(playerActor);

            UiText uiText = new UiText(mapkitItem.getDataEntry().getString(DataKey.READABLE_NAME));
            uiText.setAutoSize(true);
            add(uiText, -uiText.getTextWidth() / 2 + uiText.getCharWidth() / 2, h - 16);

            touchButton.setSize(w, h + 20);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_HOVER, TouchButtonEvent.TOUCH_HOVER, this::touchButton_touchHover);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_HOVER_OUT, TouchButtonEvent.TOUCH_HOVER_OUT, this::touchButton_touchHoverOut);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_UP, TouchButtonEvent.TOUCH_UP, this::touchButton_touchUp);
            add(touchButton, borderedRect.getX(), borderedRect.getY());

            Mouse.setVisible(true);
        }

        private void touchButton_touchUp(Event event) {
            var e = (TouchButtonEvent) event;
            if (e.isOnArea()) {
                charSelectScene.dispatchEvent(CharSelectSceneEvent.builder()
                        .type(CharSelectSceneEvent.CHARACTER_SELECT)
                        .mapkitItem(mapkitItem).build()
                );
                charSelectScene.dispose();
                charSelectScene.removeFromParent();
            }
        }

        private void touchButton_touchHover(Event event) {
            borderedRect.setBorderColor(Color.WHITE);
            playerActor.setAnimation(AnimationKey.IDLE, true);
            decorDoor.setVisible(true);
        }

        private void touchButton_touchHoverOut(Event event) {
            borderedRect.setBorderColor(Color.BLACK);
            playerActor.setAnimation(AnimationKey.WALK, true);
            decorDoor.setVisible(false);
        }

        public void dispose() {
            touchButton.setEnabled(false);
            playerActor.removeFromParent();
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        MapIO.setMapkitsDirectory("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/");
        D2D2World.init(false, false);


        CharSelectScene charSelectScene = new CharSelectScene();
        root.add(charSelectScene);

        D2D2.loop();
    }
}
