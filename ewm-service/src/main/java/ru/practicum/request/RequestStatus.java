package ru.practicum.request;

public enum RequestStatus {
    /**
     * Подтверждена
     */
    CONFIRMED,

    /**
     * Отклонена
     */
    REJECTED,

    /**
     * На рассматрении
     */
    PENDING,

    /**
     * Отменена
     */
    CANCELED
}
