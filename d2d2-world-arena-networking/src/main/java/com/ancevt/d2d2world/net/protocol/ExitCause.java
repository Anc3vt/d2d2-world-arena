
package com.ancevt.d2d2world.net.protocol;

import java.util.Arrays;

public enum ExitCause {

    NORMAL_EXIT(1),
    LOST_CONNECTION(2);

    private final int value;

    ExitCause(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ExitCause ofValue(int value) {
        return Arrays.stream(ExitCause.values()).filter(e -> e.value == value).findAny().orElseThrow();
    }
}
