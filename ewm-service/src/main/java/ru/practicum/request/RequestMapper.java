package ru.practicum.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.RequestGetDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static RequestGetDto toRequestGetDto(Request request) {
        return RequestGetDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }
}
