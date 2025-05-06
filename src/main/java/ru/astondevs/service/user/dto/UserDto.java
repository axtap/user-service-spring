package ru.astondevs.service.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class UserDto {

    @EqualsAndHashCode.Exclude
    private final long id;

    private final String name;

    private final String email;

    private final int age;

    @EqualsAndHashCode.Exclude
    private final LocalDateTime createdAt;

}