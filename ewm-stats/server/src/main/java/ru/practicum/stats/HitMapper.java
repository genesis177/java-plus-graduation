package ru.practicum.stats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatsResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {

    public static Hit toHit(HitRequestDto hitRequestDto) {
        Hit hit = new Hit();
        hit.setApp(hitRequestDto.getApp());
        hit.setUri(hitRequestDto.getUri());
        hit.setIp(hitRequestDto.getIp());
        hit.setTimestamp(hitRequestDto.getTimestamp());
        return hit;
    }

    public static HitResponseDto toHitResponseDto(Hit hit) {
        HitResponseDto dto = new HitResponseDto();
        dto.setId(hit.getId());
        dto.setApp(hit.getApp());
        dto.setUri(hit.getUri());
        dto.setIp(hit.getIp());
        dto.setTimestamp(hit.getTimestamp());
        return dto;
    }

    public static StatsResponseDto toStatsResponseDto(ViewStats viewStats) {
        StatsResponseDto responseDto = new StatsResponseDto();
        responseDto.setApp(viewStats.getApp());
        responseDto.setUri(viewStats.getUri());
        responseDto.setHits(viewStats.getHits());
        return responseDto;
    }
}
