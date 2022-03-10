package ru.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.ancevt.d2d2world.net.dto.Dto;
import ru.ancevt.d2d2world.net.dto.PlayerDto;

import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerInfoDto implements Dto {

    private final String name;
    private final int maxPlayers;
    private final String serverVersion;
    private final String protocolVersion;
    private final String currentMap;
    private final String modName;

    @Builder.Default
    private final Set<PlayerDto> players = Set.of();


}
