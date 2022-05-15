
package com.ancevt.d2d2.backend.lwjgl;

import java.util.LinkedList;
import java.util.List;

public class OSDetector {


    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {
        System.out.println("os.name: " + OS);

        if (isWindows()) {
            System.out.println("This is Windows");
        } else if (isMac()) {
            System.out.println("This is Mac");
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
        } else if (isSolaris()) {
            System.out.println("This is Solaris");
        } else {
            System.out.println("Your OS is not support!!");
        }

        List<Float> listA = new LinkedList<>();
        List<Float> listB = new LinkedList<>();

    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix")
                || OS.contains("nux")
                || OS.indexOf("aix") > 0;
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

}
