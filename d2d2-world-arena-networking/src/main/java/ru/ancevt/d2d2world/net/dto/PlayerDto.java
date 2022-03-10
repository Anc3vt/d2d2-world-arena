package ru.ancevt.d2d2world.net.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class PlayerDto implements Dto {
    private final int id;
    private final String name;
    private final int color;
    private final int ping;
    private final int frags;
    private final int playerActorGameObjectId;
}
