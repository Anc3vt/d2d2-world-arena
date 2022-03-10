package ru.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.ancevt.d2d2world.net.dto.Dto;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerActorDto implements Dto {

    private final int playerActorGameObjectId;
}
