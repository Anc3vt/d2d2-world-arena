package com.ancevt.d2d2world.net.dto.server;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.ancevt.d2d2world.net.dto.Dto;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RconResponseDto implements Dto {
    private final String text;
}
