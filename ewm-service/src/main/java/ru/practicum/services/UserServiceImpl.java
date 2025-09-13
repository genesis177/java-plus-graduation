package ru.practicum.services;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.dto.users.UserCreateDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("Запрос списка пользователей на уровне сервиса");
        log.info("Получена коллекция идентификаторов пользователей размером {}", ids == null ? "пустая" : ids.size());
        log.info("Получен номер начального элемента: {}", from);
        log.info("Получен максимальный размер коллекции: {}", size);

        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Direction.ASC, "id"));
        Collection<User> searchResult;
        if (ids == null || ids.isEmpty()) {
            searchResult = userRepository.findAll(pageRequest).getContent();
        } else {
            searchResult = userRepository.findAllByIds(ids, pageRequest).getContent();
        }
        log.info("Из хранилища получена коллекция размером {}", searchResult.size());

        Collection<UserDto> result = searchResult.stream()
                .map(userMapper::mapToUserDto)
                .toList();
        log.info("Полученная коллекция преобразована. Размер коллекции после преобразования {}", result.size());

        log.info("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public UserDto createUser(UserCreateDto dto) {
        log.info("Создание пользователя на уровне сервиса");

        User user = userMapper.mapToUser(dto);
        log.info("Несохраненная модель преобразована");

        log.info("Валидация несохраненной модели");
        validateUser(user);
        log.info("Валидация несохраненной модели завершена");

        user = userRepository.save(user);
        log.info("Сохранение модели завершено. Получено идентификатор {}", user.getId());

        UserDto result = userMapper.mapToUserDto(user);
        log.info("Сохраненная модель преобразована. Идентификатор модели после преобразования {}", result.getId());

        log.info("Возврат результатов создания пользователя на уровень контроллера");
        return result;
    }

    @Override
    public void deleteUser(long userId) {
        log.warn("Удаление пользователя по идентификатору на уровне сервиса");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        log.info("Передан идентификатор удаляемого пользователя: {}", user.getId());

        userRepository.deleteById(user.getId());
        log.info("Удаление пользователя завершено");

        log.info("Возврат результатов удаления пользователя на уровень контроллера");
    }

    /**
     * Метод проверяет правильность заполнения данных модели {@link User}
     *
     * @param user модель {@link User}
     */
    private void validateUser(User user) {
        log.info("Валидация почты модели");
        validateUserEmail(user);
        log.info("Валидация почты завершена");
    }

    /**
     * Метод проверяет правильность заполнения почты пользователя
     *
     * @param user модель {@link User}
     */
    private void validateUserEmail(User user) {
        boolean exists;
        if (user.getId() == null) {
            exists = userRepository.existsByEmailIgnoreCase(user.getEmail());
        } else {
            exists = userRepository.existsByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId());
        }

        if (exists) {
            throw new ConflictException("email " + user.getEmail() + " already used.");
        }

        log.info("Передано корректное значение поты: {}", user.getEmail());
    }
}
