
package com.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.PlayerDto;

import java.time.LocalDateTime;
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
    private final LocalDateTime startTime;

    @Builder.Default
    private final Set<PlayerDto> players = Set.of();
}
