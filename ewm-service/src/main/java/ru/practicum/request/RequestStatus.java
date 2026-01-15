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
     * На рассмотрении
     */
    PENDING,

    /**
     * Отменена
     */
    CANCELED
}
