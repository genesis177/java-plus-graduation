package ru.practicum.user;

import java.util.Collection;
import java.util.List;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;

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
     * @throws ConflictException если переданный адрес почты уже используется
     */
    UserDto createUser(UserCreateDto dto) throws ConflictException;

    /**
     * Метод проверяет возможность удаления пользователя и передаёт для удаления по его идентификатору
     *
     * @param userId идентификатор удаляемого пользователя
     * @throws NotFoundException если пользователь не найден по переданному идентификатору
     */
    void deleteUser(long userId) throws NotFoundException;
}
