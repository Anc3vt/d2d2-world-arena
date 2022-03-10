package ru.ancevt.d2d2world.net.dto.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.ancevt.d2d2world.net.dto.Dto;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerEnterRequestDto implements Dto {

    private final String name;
    private final String protocolVersion;
}
