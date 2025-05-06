package ru.astondevs.service.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.entity.UserEntity;
import ru.astondevs.service.user.mapper.UserMapper;
import ru.astondevs.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserDto create(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toDto(savedUserEntity);
    }

    public UserDto update(Long id, UserDto userDto) {
        UserEntity existingUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUserEntity.setName(userDto.getName());
        existingUserEntity.setEmail(userDto.getEmail());
        existingUserEntity.setAge(userDto.getAge());

        UserEntity updatedUserEntity = userRepository.save(existingUserEntity);
        return userMapper.toDto(updatedUserEntity);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
