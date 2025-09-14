package ru.practicum.user;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Метод возвращает коллекцию {@link User} по переданному списку идентификаторов, с учетом начального номера
     * элемента, размера коллекции и правил сортировки
     *
     * @param ids коллекция идентификаторов пользователей
     * @param pageable правила выборки и сортировки
     * @return коллекция {@link User}
     */
    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    /**
     * Метод проверяет использование почтового адреса зарегистрированными пользователями
     *
     * @param email почтовый адрес
     * @return результат проверки
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Метод проверяет использование почтового адреса другими зарегистрированными пользователями, кроме пользователя с
     * переданным идентификатором
     *
     * @param email почтовый адрес
     * @param userId идентификатор исключенного пользователя
     * @return результат проверки
     */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long userId);
}
