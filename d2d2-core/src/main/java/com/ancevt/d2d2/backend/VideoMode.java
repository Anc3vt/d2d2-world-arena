package com.ancevt.d2d2.backend;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class VideoMode {
    private final int width;
    private final int height;
    private final int refreshRate;

    public String getResolution() {
        return width + "x" + height;
    }

    @Override
    public String toString() {
        return "VideoMode{" +
                "width=" + width +
                ", height=" + height +
                ", refreshRate=" + refreshRate +
                '}';
    }
}
