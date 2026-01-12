package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring") // автоматически регистрируется в Spring
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto userDto);
}