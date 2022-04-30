package com.ancevt.d2d2.backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoMode {
    private final int width;
    private final int height;
    private final int refreshRate;

    @Override
    public String toString() {
        return "VideoMode{" +
                "width=" + width +
                ", height=" + height +
                ", refreshRate=" + refreshRate +
                '}';
    }
}
