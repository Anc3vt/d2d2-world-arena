
package com.ancevt.d2d2world.tools;

import com.ancevt.d2d2world.gameobject.Scenery;
import com.ancevt.util.args.Args;

import static java.lang.String.format;

public class SceneryMapkitItemGenerator {

    public static void main(String[] args) {
        Args a = Args.of(args);

        if (a.contains("--help")) {
            System.out.println("[-x 0 -y 0] -w 128 -h 128 [-sw 16 -sh 16] -p prefix -a atlas-name.png ");
            System.exit(0);
        }

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
                        "name = %s%s | class = %s | atlas = %s | idle=%d,%d,%d,%d",
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
















