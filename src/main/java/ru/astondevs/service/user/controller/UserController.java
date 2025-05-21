package ru.astondevs.service.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.hateoas.UserModelAssembler;
import ru.astondevs.service.user.service.UserService;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Управление пользователями: создание, удаление, обновление, поиск")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе по его уникальному идентификатору")
    @GetMapping("/{id}")
    public EntityModel<UserDto> findById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        UserDto userDto = userService.findById(id);
        return userModelAssembler.toModel(userDto);
    }

    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя и возвращает его данные")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового пользователя")
            @RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);
        return userModelAssembler.toModel(created);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя по ID")
    @PutMapping("/{id}")
    public EntityModel<UserDto> update(
            @Parameter(description = "ID пользователя для обновления", example = "1")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновлённые данные пользователя")
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        UserDto updated = userService.update(id, userDto);
        return userModelAssembler.toModel(updated);
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
    public CollectionModel<EntityModel<UserDto>> findAll() {
        List<EntityModel<UserDto>> users = userService.findAll().stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }
}
