
package com.ancevt.d2d2world.net.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class PlayerDto implements Dto {
    private final int id;
    private final String name;
    private final Integer color;
    private final Integer ping;
    private final Integer frags;
    private final Integer playerActorGameObjectId;
}
