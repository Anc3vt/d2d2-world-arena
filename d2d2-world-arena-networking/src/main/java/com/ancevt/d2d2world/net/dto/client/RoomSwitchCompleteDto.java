
package com.ancevt.d2d2world.net.dto.client;

import com.ancevt.d2d2world.net.dto.Dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomSwitchCompleteDto implements Dto {
}
