package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.users.UserCreateDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.model.User;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User mapToUser(UserCreateDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
