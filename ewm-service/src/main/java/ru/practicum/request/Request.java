package ru.practicum.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import java.time.LocalDateTime;

/**
 * Событие.
 */
@Table(name = "requests")
@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата время создания
     */
    @Column(name = "created")
    private LocalDateTime created;

    /**
     * Событие
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Пользователь, создавший запрос
     */
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    /**
     * Статус
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;
}
