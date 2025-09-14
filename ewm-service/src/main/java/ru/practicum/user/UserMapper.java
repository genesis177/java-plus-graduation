package ru.practicum.user;

import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;

public interface UserMapper {

    /**
     * Метод преобразует модель {@link User} в модель {@link UserDto}
     *
     * @param user модель {@link User}
     * @return модель {@link UserDto}
     */
    UserDto mapToUserDto(User user);

    /**
     * Метод преобразует модель {@link UserCreateDto} в модель {@link User}
     *
     * @param dto модель {@link UserCreateDto}
     * @return модель {@link User}
     */
    User mapToUser(UserCreateDto dto);
}
