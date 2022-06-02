/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
