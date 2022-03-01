/*
 *   D2D2 World
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
package ru.ancevt.d2d2world.tools;

import ru.ancevt.d2d2world.gameobject.Scenery;
import ru.ancevt.util.args.Args;

import static java.lang.String.format;

public class SceneryMapkitItemsGenerator {

    public static void main(String[] args) {
        Args a = new Args(args);

        int sceneryWidth = a.get(int.class, "-sw", 16);
        int sceneryHeight = a.get(int.class, "-sh", 16);

        int areaX = a.get(int.class, "-x", 0);
        int areaY = a.get(int.class, "-y", 0);
        int areaWidth = a.get(int.class, "-w", 0);
        int areaHeight = a.get(int.class, "-h", 0);

        if (areaWidth == 0 || areaHeight == 0) {
            System.out.println("Please specify atlas -w(idth) and -h(eight)");
            System.exit(0);
        }

        String namePrefix = a.get(String.class, "-p", null);
        if (namePrefix == null) {
            System.out.println("Please specify mapkit item id -p(prefix)");
            System.exit(0);
        }

        String textureAtlas = a.get(String.class, "-a", null);
        if (textureAtlas == null) {
            System.out.println("Please specify texture -a(tlas) file name");
            System.exit(0);
        }

        String clazz = a.get(String.class, "-c", Scenery.class.getName());

        int idSuffix = 0;
        for (int y = areaY; y < areaY + areaHeight; y += sceneryHeight) {
            for (int x = areaX; x < areaX + areaWidth; x += sceneryWidth) {
                String line = format(
                        "id = %s%s | class = %s | atlas = %s | idle=%d,%d,%d,%d",
                        namePrefix,
                        idSuffix,
                        clazz,
                        textureAtlas,
                        x,
                        y,
                        sceneryWidth,
                        sceneryHeight
                );
                print(line);

                idSuffix++;
            }
        }
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
















