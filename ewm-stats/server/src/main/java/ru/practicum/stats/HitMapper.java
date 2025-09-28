package ru.practicum.stats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.HitDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());
        return hit;
    }

    public static HitDto toHitDto(Hit hit) {
        HitDto dto = new HitDto();
        dto.setId(hit.getId());
        dto.setApp(hit.getApp());
        dto.setUri(hit.getUri());
        dto.setIp(hit.getIp());
        dto.setTimestamp(hit.getTimestamp());
        return dto;
    }
}
