package ru.astondevs.service.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto createTestUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .name("Test User")
                .email("test_user@example.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void checkFindById() throws Exception {
        UserDto userDto = createTestUserDto(1L);

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.age").value(userDto.getAge()));
//                .andExpect(jsonPath("$.createdAt").value(userDto.getCreatedAt()));
    }

    @Test
    void checkCreate() throws Exception {
        UserDto newUser = UserDto.builder()
                .name("New User")
                .email("new_user@test.com")
                .age(25)
                .build();

        UserDto savedUser = newUser.toBuilder().id(1L).build();

        when(userService.create(any(UserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void checkUpdate() throws Exception {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .name("New User")
                .email("new_user@test.com")
                .age(48)
                .build();

        UserDto updatedUser = createTestUserDto(userId).toBuilder()
                .name("Updated User")
                .email("updated_user@test.com")
                .age(35)
                .build();

        when(userService.update(eq(userId), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated_user@test.com"))
                .andExpect(jsonPath("$.age").value(35));
    }

    @Test
    void checkDelete() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkFindAll() throws Exception {
        List<UserDto> users = List.of(
                createTestUserDto(1L),
                createTestUserDto(2L)
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

}