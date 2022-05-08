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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IdGenerator {

    private static IdGenerator instance;

    public static IdGenerator getInstance() {
        return instance == null ? instance = new IdGenerator() : instance;
    }

    private final List<Integer> ids;

    private IdGenerator() {
        ids = new CopyOnWriteArrayList<>();
    }

    public synchronized int getNewId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if(!ids.contains(i)) {
                ids.add(i);
                return i;
            }
        }
        throw new IllegalStateException("All ids taken");
    }

    public void clear() {
        ids.clear();
    }

    public void addId(int id) {
        ids.add(id);
    }

    public void removeId(int id) {
        ids.removeAll(List.of(id));
    }

    public boolean contains(int id) {
        return ids.contains(id);
    }


}
