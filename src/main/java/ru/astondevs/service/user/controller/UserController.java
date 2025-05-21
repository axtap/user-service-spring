package ru.astondevs.service.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Управление пользователями: создание, удаление, обновление, поиск")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе по его уникальному идентификатору")
    @GetMapping("/{id}")
    public UserDto findById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя и возвращает его данные")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового пользователя")
            @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя по ID")
    @PutMapping("/{id}")
    public UserDto update(
            @Parameter(description = "ID пользователя для обновления", example = "1")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновлённые данные пользователя")
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его идентификатору")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID пользователя для удаления", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
    }

    @Operation(summary = "Получить список всех пользователей", description = "Возвращает список всех пользователей в системе")
    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }
}
