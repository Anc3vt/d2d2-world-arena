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
package com.ancevt.d2d2world.client.scene.charselect;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.interactive.InteractiveContainer;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.data.DataKey;
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

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2world.client.config.ClientConfig.CONFIG;
import static com.ancevt.d2d2world.client.config.ClientConfig.DEBUG_CHARACTER;

public class CharSelectScene extends Container {

    private final PlainRect bg;
    private final Set<CharSelectItem> charSelectItems;

    public CharSelectScene() {
        bg = new PlainRect(Color.of(0x000011));
        add(bg);
        charSelectItems = new HashSet<>();

        BitmapText uiLabel = new BitmapText("Select a character:");
        uiLabel.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        add(uiLabel, 10, 14);

        addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);
        setScale(1.5f, 1.5f);
    }

    public void dispose() {
        for (CharSelectItem charSelectItem : charSelectItems) {
            charSelectItem.dispose();
        }
    }

    private void this_addToStage(Event event) {
        removeEventListener(this, Event.ADD_TO_STAGE);

        final float sw = stage().getWidth();
        final float sh = stage().getHeight();

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

        CONFIG.ifContains(DEBUG_CHARACTER, value -> {
            MapkitItem mapkitItem = BuiltInMapkit.getInstance().getItemById(value);
            dispatchEvent(CharSelectSceneEvent.builder()
                    .type(CharSelectSceneEvent.CHARACTER_SELECT)
                    .mapkitItem(mapkitItem).build()
            );
            dispose();
            removeFromParent();
        });

    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class CharSelectSceneEvent extends Event {

        public static final String CHARACTER_SELECT = "characterSelect";

        private final MapkitItem mapkitItem;
    }

    private static class CharSelectItem extends Container {

        private final MapkitItem mapkitItem;
        private final CharSelectScene charSelectScene;
        private final BorderedRect borderedRect;
        private final InteractiveContainer interactiveButton;
        private final PlayerActor playerActor;
        private final Sprite decorDoor;

        public CharSelectItem(@NotNull MapkitItem mapkitItem, CharSelectScene charSelectScene) {
            this.mapkitItem = mapkitItem;
            this.charSelectScene = charSelectScene;

            decorDoor = new Sprite(D2D2WorldArenaClientAssets.getCharSelectDoorTexture());

            playerActor = (PlayerActor) mapkitItem.createGameObject(0);
            playerActor.setAnimation(AnimationKey.WALK, true);
            playerActor.setHealthBarVisible(false);

            borderedRect = new BorderedRect(mapkitItem.getTexture().width(), mapkitItem.getTexture().height(), Color.of(0x111111), Color.BLACK);

            addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);

            interactiveButton = new InteractiveContainer();
        }

        private void this_addToStage(Event event) {
            removeEventListener(this, Event.ADD_TO_STAGE);

            final float w = mapkitItem.getTexture().width();
            final float h = mapkitItem.getTexture().height();

            add(borderedRect, -w / 2, -h / 2);

            decorDoor.setVisible(false);
            add(decorDoor, 8, -h / 2);

            playerActor.setWeaponVisible(false);
            add(playerActor);

            BitmapText uiText = new BitmapText(mapkitItem.getDataEntry().getString(DataKey.READABLE_NAME));
            uiText.setBitmapFont(ComponentFont.getBitmapFontMiddle());
            uiText.setAutosize(true);
            add(uiText, -uiText.getTextWidth() / 2f + uiText.getCharWidth() / 2, h - 16);

            interactiveButton.setSize(w, h + 20);
            interactiveButton.addEventListener(this, InteractiveEvent.UP, this::interactiveButton_up);
            interactiveButton.addEventListener(this, InteractiveEvent.OUT, this::interactiveButton_out);
            interactiveButton.addEventListener(this, InteractiveEvent.HOVER, this::interactiveButton_hover);
            add(interactiveButton, borderedRect.getX(), borderedRect.getY());

            Mouse.setVisible(true);
        }

        private void interactiveButton_up(Event event) {
            var e = (InteractiveEvent) event;
            if (e.isOnArea()) {
                charSelectScene.dispatchEvent(CharSelectSceneEvent.builder()
                        .type(CharSelectSceneEvent.CHARACTER_SELECT)
                        .mapkitItem(mapkitItem).build()
                );
                charSelectScene.dispose();
                charSelectScene.removeFromParent();
            }
        }

        private void interactiveButton_hover(Event event) {
            borderedRect.setBorderColor(Color.WHITE);
            playerActor.setAnimation(AnimationKey.IDLE, true);
            decorDoor.setVisible(true);
        }

        private void interactiveButton_out(Event event) {
            borderedRect.setBorderColor(Color.BLACK);
            playerActor.setAnimation(AnimationKey.WALK, true);
            decorDoor.setVisible(false);
        }

        public void dispose() {
            interactiveButton.setEnabled(false);
            playerActor.removeFromParent();
        }
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        MapIO.setMapkitsDirectory("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/");
        D2D2World.init(false, false);


        CharSelectScene charSelectScene = new CharSelectScene();
        stage.add(charSelectScene);

        D2D2.loop();
    }
}
