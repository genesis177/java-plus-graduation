package ru.practicum.user;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;

/**
 * API для работы с пользователями
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Получение информации о пользователях
     *
     * @param ids  id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return коллекция {@link UserDto}
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                        HttpServletResponse response) {
        Page<UserDto> page = userService.getUsers(ids, from, size);
        response.setHeader("X-Total-Count", String.valueOf(page.getTotalElements()));

        return page.getContent();
    }

    /**
     * Добавление нового пользователя
     *
     * @param dto Данные добавляемого пользователя
     * @return Данные добавленного пользователя
     * @throws ConflictException если переданный адрес почты уже используется
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreateDto dto) throws ConflictException {

        return userService.createUser(dto);
    }

    /**
     * Удаление пользователя
     *
     * @param userId id пользователя
     * @throws NotFoundException если пользователь не найден по переданному идентификатору
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) throws NotFoundException {
        userService.deleteUser(userId);
    }
}
