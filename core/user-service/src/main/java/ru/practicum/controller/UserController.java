package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserDto;
import ru.practicum.feign.UserClient;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController implements UserClient {
    private final UserService userService;

    @Override
    public UserDto createUser(UserDto createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}