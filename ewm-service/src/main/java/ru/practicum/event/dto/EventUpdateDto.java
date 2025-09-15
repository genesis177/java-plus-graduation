package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.enums.StateActions;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {

    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActions stateAction;
    private String title;

    public boolean hasAnnotation() {
        return annotation != null && !annotation.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasEventDate() {
        return eventDate != null && !eventDate.isBlank();
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasStateAction() {
        return stateAction != null;
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
