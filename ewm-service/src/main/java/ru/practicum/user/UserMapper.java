package ru.practicum.user;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

@Slf4j
@UtilityClass
public class UserMapper {

    public UserDto mapToUserDto(User user) {
        log.info("Преобразование модели {} в модель {} ", User.class, UserDto.class);
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User mapToUser(UserCreateDto dto) {
        log.info("Преобразование модели {} в  модель {}", UserCreateDto.class, User.class);
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserShortDto mapToUserShortDto(User user) {
        log.info("Преобразование модели {} в краткую модель {}", User.class, UserShortDto.class);
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
