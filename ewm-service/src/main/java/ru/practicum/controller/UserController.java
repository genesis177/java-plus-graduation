package ru.practicum.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.users.UserCreateDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.services.UserService;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Обработка GET-запроса к /admin/users
     *
     * @param ids список идентификаторов пользователей
     * @param from номер начального элемента
     * @param size максимальный размер коллекции
     * @return коллекция {@link UserDto}
     */
    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        Collection<UserDto> result = userService.getUsers(ids, from, size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка POST-запроса к /admin/users
     *
     * @param dto несохраненный экземпляр класса {@link UserCreateDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto dto) {
        UserDto result = userService.createUser(dto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /**
     * Обработка DELETE-запроса к /admin/users/{userId}
     *
     * @param userId идентификатор удаляемого пользователя
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
