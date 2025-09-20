package ru.practicum.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.enums.States;
import ru.practicum.user.User;

/**
 * Событие.
 */
@Table(name = "events")
@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое описание
     */
    @Column(name = "annotation")
    private String annotation;

    /**
     * Категория
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Полное описание
     */
    @Column(name = "description")
    private String description;

    /**
     * Дата проведения
     */
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    /**
     * Место проведения
     */
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;

    /**
     * Признак оплаты
     */
    @Column(name = "paid")
    private Boolean paid;

    /**
     * Максимальное количество участников
     */
    @Column(name = "participant_limit")
    private Integer participantLimit;

    /**
     * Признак модерации заявок
     */
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    /**
     * Заголовок
     */
    @Column(name = "title")
    private String title;

    /**
     * Дата создания
     */
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    /**
     * Дата публикации
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Инициатор
     */
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    /**
     * Состояние
     */
    @Enumerated(EnumType.STRING)
    private States state;
}
