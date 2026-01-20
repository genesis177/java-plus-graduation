package ru.practicum.service;

import ru.practicum.dto.user.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto userDto);

    Collection<UserDto> getUsers(Collection<Long> ids, Integer from, Integer size);

    UserDto getUserById(Long userId);

    void deleteUser(Long id);
}