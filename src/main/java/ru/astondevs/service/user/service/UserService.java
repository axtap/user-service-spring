package ru.astondevs.service.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.entity.UserEntity;
import ru.astondevs.service.user.event.UserEvent;
import ru.astondevs.service.user.event.UserEventType;
import ru.astondevs.service.user.mapper.UserMapper;
import ru.astondevs.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserDto create(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Отправка события в Kafka
        kafkaTemplate.send("user-events",
            UserEvent.builder()
                    .type(UserEventType.CREATED)
                    .email(savedUserEntity.getEmail())
                    .build());


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
        UserEntity user = userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.deleteById(id);

        // Отправка события в Kafka
        kafkaTemplate.send("user-events",
                UserEvent.builder()
                        .type(UserEventType.DELETED)
                        .email(user.getEmail())
                        .build());
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
