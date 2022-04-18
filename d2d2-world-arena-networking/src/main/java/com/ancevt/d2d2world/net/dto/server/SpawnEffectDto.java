package com.ancevt.d2d2world.net.dto.server;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SpawnEffectDto implements Dto {
    private final float x;
    private final float y;
}
