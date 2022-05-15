
package com.ancevt.d2d2world.net.dto.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.ancevt.d2d2world.net.dto.Dto;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerExitRequestDto implements Dto {
    public static final PlayerExitRequestDto INSTANCE = new PlayerExitRequestDto();
}
