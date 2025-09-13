package ru.practicum.services;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import ru.practicum.dto.users.UserCreateDto;
import ru.practicum.dto.users.UserDto;

public interface UserService {

    /**
     * Метод возвращает коллекцию {@link UserDto} на основе переданных параметров
     *
     * @param ids список идентификаторов пользователей
     * @param from номер начального элемента
     * @param size максимальный размер коллекции
     * @return коллекция {@link UserDto}
     */
    Collection<UserDto> getUsers(List<Long> ids, int from, int size);

    /**
     * Метод получает несохранённый экземпляр класса {@link UserCreateDto}, проверяет его, передает для сохранения и
     * возвращает экземпляр класса {@link UserDto} после сохранения
     *
     * @param dto несохраненный экземпляр класса {@link UserCreateDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    UserDto createUser(@Valid UserCreateDto dto);

    /**
     * Метод проверяет возможность удаления пользователя и передаёт для удаления по его идентификатору
     *
     * @param userId идентификатор удаляемого пользователя
     */
    void deleteUser(long userId);
}
