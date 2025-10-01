package ru.practicum.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.Event;
import ru.practicum.user.User;

/**
 * Комментарий.
 */
@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Автор
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * Комментируемое событие
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Содержимое
     */
    @Column(name = "text")
    private String text;

    /**
     * Дата создания
     */
    @Column(name = "created_on")
    private LocalDateTime createdOn;
}
