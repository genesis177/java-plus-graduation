package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;

@Component
@Slf4j
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mapToUserDto(User user) {
        log.info("Преобразование модели {} в модель {} ", User.class, UserDto.class);
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User mapToUser(UserCreateDto dto) {
        log.info("Преобразование модели {} в  модель {}", UserCreateDto.class, User.class);
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
