package ru.ancevt.d2d2world.net.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class PlayerActorDto implements ExtraDto {

    private final int playerActorGameObjectId;

}
