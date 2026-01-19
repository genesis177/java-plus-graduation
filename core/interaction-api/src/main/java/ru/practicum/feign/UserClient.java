package ru.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@Valid @RequestBody UserDto createUserRequest);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Collection<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserDto getUserById(@PathVariable Long userId);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long userId) throws FeignException;

}