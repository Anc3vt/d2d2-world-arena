package ru.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.ancevt.d2d2world.net.dto.Dto;
import ru.ancevt.d2d2world.net.dto.PlayerDto;
import ru.ancevt.d2d2world.net.protocol.ExitCause;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerExitDto implements Dto {
    
    private final PlayerDto player;
    private final ExitCause exitCause;
}
