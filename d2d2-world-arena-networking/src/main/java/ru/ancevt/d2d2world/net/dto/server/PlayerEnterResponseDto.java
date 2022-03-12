package ru.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.ancevt.d2d2world.net.dto.Dto;
import ru.ancevt.d2d2world.net.dto.PlayerDto;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerEnterResponseDto implements Dto {
    private final PlayerDto player;
    private final String protocolVersion;
    private final int color;
    private final LocalDateTime serverStartTime;
}
