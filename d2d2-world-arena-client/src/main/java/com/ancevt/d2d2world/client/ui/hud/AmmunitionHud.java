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
package com.ancevt.d2d2world.client.ui.hud;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import org.jetbrains.annotations.NotNull;

public class AmmunitionHud extends Container {
    private final BitmapText uiAmmunition;
    private final Sprite weaponSprite;

    public AmmunitionHud() {
        uiAmmunition = new BitmapText();
        uiAmmunition.setBitmapFont(ComponentFont.getBitmapFontMiddle());
        uiAmmunition.setText("~");
        add(uiAmmunition, 35, 16-8);

        weaponSprite = new Sprite();
        add(weaponSprite);
    }

    public void updateFor(@NotNull Actor actor) {
        final Weapon weapon = actor.getCurrentWeapon();

        weaponSprite.setTexture(weapon.getTexture());
        uiAmmunition.setText(weapon.getAmmunition() + "");

        if(weapon.getAmmunition() < 25) {
            uiAmmunition.setColor(Color.RED);
        } else if(weapon.getAmmunition() < 50) {
            uiAmmunition.setColor(Color.YELLOW);
        } else {
            uiAmmunition.setColor(Color.WHITE);
        }


    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        var a = new AmmunitionHud();
        stage.add(a);
        D2D2.loop();
    }
}
