
package com.ancevt.d2d2world.net.dto.server;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SetRoomDto implements Dto {

    private final String roomId;
    private final float cameraX;
    private final float cameraY;
}
