package ru.practicum.request;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.RequestGetDto;

@UtilityClass
public final class RequestMapper {

    public RequestGetDto toRequestGetDto(Request request) {
        return RequestGetDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }
}
