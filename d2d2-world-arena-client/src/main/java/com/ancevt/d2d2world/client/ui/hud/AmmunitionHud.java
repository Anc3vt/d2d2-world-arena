/*
 *   D2D2 World Arena Client
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
package com.ancevt.d2d2world.client.ui.hud;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.client.ui.UiText;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import org.jetbrains.annotations.NotNull;

public class AmmunitionHud extends DisplayObjectContainer {
    private final UiText uiAmmunition;
    private final Sprite weaponSprite;

    public AmmunitionHud() {
        uiAmmunition = new UiText();
        uiAmmunition.setText("~");
        add(uiAmmunition, 35, 16-8);

        weaponSprite = new Sprite();
        add(weaponSprite);
    }

    public void updateFor(@NotNull Actor actor) {
        final Weapon weapon = actor.getCurrentWeapon();

        weaponSprite.setTexture(weapon.getTexture());
        uiAmmunition.setText(weapon.getAmmunition());

        if(weapon.getAmmunition() < 25) {
            uiAmmunition.setColor(Color.RED);
        } else if(weapon.getAmmunition() < 50) {
            uiAmmunition.setColor(Color.DARK_YELLOW);
        } else {
            uiAmmunition.setColor(Color.WHITE);
        }


    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating"));

        var a = new AmmunitionHud();

        root.add(a);

        D2D2.loop();
    }
}
