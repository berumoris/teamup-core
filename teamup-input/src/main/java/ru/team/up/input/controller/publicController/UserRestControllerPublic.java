package ru.team.up.input.controller.publicController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team.up.core.entity.Account;
import ru.team.up.core.entity.Event;
import ru.team.up.core.entity.User;
import ru.team.up.core.exception.EventsNotFoundIdException;
import ru.team.up.core.exception.UserNotFoundEmailException;
import ru.team.up.core.exception.UserNotFoundIDException;
import ru.team.up.core.exception.UserNotFoundUsernameException;
import ru.team.up.core.mappers.EventMapper;
import ru.team.up.core.mappers.UserMapper;
import ru.team.up.dto.EventDto;
import ru.team.up.input.payload.request.UserRequest;
import ru.team.up.input.response.EventDtoResponse;
import ru.team.up.input.service.UserServiceRest;
import ru.team.up.input.response.UserDtoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST-контроллер для пользователей
 *
 * @author Pavel Kondrashov
 */

@Slf4j
@Tag(name = "User Public Rest Controller", description = "User API")
@RestController
@RequestMapping("public/user")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserRestControllerPublic {
    private final UserServiceRest userServiceRest;

    /**
     * Метод для поиска пользователя по id
     *
     * @param userId id пользователя
     * @return Ответ поиска и статус
     */
    @Operation(summary = "Получение пользователя по id")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") Long userId) {
        log.debug("Запрос на поиск пользователя с id = {}", userId);

        Optional<Account> userOptional = Optional.ofNullable(userServiceRest.getUserById(userId));

        return userOptional
                .map(user -> new ResponseEntity<>(
                        UserDtoResponse.builder().userDto(UserMapper.INSTANCE.mapUserToDto((User) user)).build(),
                        HttpStatus.OK))
                .orElseThrow(() -> new UserNotFoundIDException(userId));
    }


    /**
     * Метод поиска пользователя по почте
     *
     * @param userEmail почта пользователя
     * @return Ответ поиска и статус проверки
     */
    @Operation(summary = "Поиск пользователя по email")
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> getUserByEmail(@PathVariable(value = "email") String userEmail) {
        log.debug("Запрос на поиск пользователя с почтой: {}", userEmail);
        Optional<Account> userOptional = Optional.ofNullable(userServiceRest.getUserByEmail(userEmail));

        return userOptional
                .map(user -> new ResponseEntity<>(
                        UserDtoResponse.builder().userDto(UserMapper.INSTANCE.mapUserToDto((User) user)).build(),
                        HttpStatus.OK))
                .orElseThrow(() -> new UserNotFoundEmailException(userEmail));
    }

    /**
     * Метод поиска пользователя по имени
     *
     * @param userUsername имя пользователя
     * @return Ответ поиска и статус проверки
     */
    @Operation(summary = "Поиск пользователя по имени")
    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> getUserByUsername(@PathVariable(value = "username") String userUsername) {
        log.debug("Запрос на поиск пользователя с именем: {}", userUsername);
        Optional<Account> userOptional = Optional.ofNullable(userServiceRest.getUserByUsername(userUsername));

        return userOptional
                .map(user -> new ResponseEntity<>(
                        UserDtoResponse.builder().userDto(UserMapper.INSTANCE.mapUserToDto((User) user)).build(),
                        HttpStatus.OK))
                .orElseThrow(() -> new UserNotFoundUsernameException(userUsername));
    }

    /**
     * Метод поиска всех пользователей
     *
     * @return Ответ поиска и статус проверки
     */
    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping("/")
    public ResponseEntity<List<Account>> getUsersList() {
        log.debug("Получен запрос на список всех пользоватей");
        List<Account> users = userServiceRest.getAllUsers();

        if (users.isEmpty()) {
            log.error("Список пользователей пуст");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.debug("Список пользователей получен");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Метод поиска мероприятий пользователя
     *
     * @param id id пользователя
     * @return Ответ поиска и статус проверки
     */
    @Operation(summary = "Поиск мероприятий по id пользователя")
    @GetMapping(value = "/event/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDtoResponse> getAllEventsByUserId(@PathVariable Long id) {
        log.debug("Запрос на поиск мероприятий пользователя с id: {}", id);
        List<Event> events = new ArrayList<>(userServiceRest.getAllEventsByAuthorId(id));

        return new ResponseEntity<>(EventDtoResponse.builder().eventDto(EventMapper.INSTANCE.mapEventsToDtoEventList(events)).build(),
                        HttpStatus.OK);
    }

    /**
     * Метод обновления пользователя
     *
     * @param user   Данные пользователя для изменения
     * @param userId идентификатор пользователя
     * @return Ответ обновления и статус проверки
     */
    @Operation(summary = "Изменение пользователя")
    @PutMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> updateUser(@RequestBody UserRequest user, @PathVariable("id") Long userId) {
        log.debug("Получен запрос на обновление пользователя");
        Account existUser = userServiceRest.getUserById(userId);

        if (existUser == null) {
            log.error("Пользователь не найден");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Account newUser = userServiceRest.updateUser(user, existUser.getId());
        log.debug("Пользователь обновлен");
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    /**
     * Метод для удаления пользователя
     *
     * @param userId идентификатор пользователя
     * @return Ответ удаления и статус проверки
     */
    @Operation(summary = "Удаление пользователя")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> deleteUserById(@PathVariable("id") Long userId) {
        log.debug("Получен запрос на удаления пользователя с id = {}", userId);
        Account user = userServiceRest.getUserById(userId);

        if (user == null) {
            log.error("Пользователь с id = {} не найден", userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        userServiceRest.deleteUserById(userId);

        log.debug("Пользователь с id = {} успешно удален", userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
