package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

@Component
@Slf4j
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        log.info("Преобразование модели {} в модель {} ", User.class, UserDto.class);
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToUser(UserCreateDto dto) {
        log.info("Преобразование модели {} в  модель {}", UserCreateDto.class, User.class);
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserShortDto mapToUserShortDto(User user) {
        log.info("Преобразование модели {} в краткую модель {}", User.class, UserShortDto.class);
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
