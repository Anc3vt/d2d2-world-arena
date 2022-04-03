/*
 *   D2D2 World
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
package com.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;

public interface IActioned extends IGameObject {

    @Property
    void setActionProgramData(@NotNull String actionProgramData);

    @Property
    @NotNull String getActionProgramData();

    @NotNull ActionProgram getActionProgram();
}
