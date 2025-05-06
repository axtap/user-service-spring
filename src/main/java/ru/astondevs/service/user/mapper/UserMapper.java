package ru.astondevs.service.user.mapper;

import org.springframework.stereotype.Component;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.entity.UserEntity;

@Component
public class UserMapper {

    // Преобразование UserEntity -> UserDto
    public UserDto toDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .age(entity.getAge())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // Преобразование UserDto -> UserEntity
    public UserEntity toEntity(UserDto dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}