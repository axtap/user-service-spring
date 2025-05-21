package ru.astondevs.service.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.astondevs.service.user.dto.UserDto;
import ru.astondevs.service.user.entity.UserEntity;
import ru.astondevs.service.user.event.UserEvent;
import ru.astondevs.service.user.event.UserEventType;
import ru.astondevs.service.user.mapper.UserMapper;
import ru.astondevs.service.user.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserDto findById(Long id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new EntityNotFoundException("User not found");
                });
    }

    public UserDto create(UserDto userDto) {
        log.info("Создание пользователя с email: {}", userDto.getEmail());
        UserEntity userEntity = userMapper.toEntity(userDto);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Отправка события в Kafka
        UserEvent event = UserEvent.builder()
                .type(UserEventType.CREATED)
                .email(savedUserEntity.getEmail())
                .build();

        log.info("Отправка события создания пользователя в Kafka. email: {}, topic: user-events", savedUserEntity.getEmail());
        CompletableFuture<SendResult<String, UserEvent>> future = kafkaTemplate.send("user-events", event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Событие CREATED для пользователя {} успешно отправлено в Kafka, topic = {}, partition = {}, offset = {}",
                        event.getEmail(), metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error("Ошибка при отправке события создания пользователя в Kafka. Email: {}",
                        savedUserEntity.getEmail(), ex);
            }
        });

        log.info("Создан пользователь с ID: {}", savedUserEntity.getId());
        return userMapper.toDto(savedUserEntity);
    }

    public UserDto update(Long id, UserDto userDto) {
        log.info("Обновление пользователя с ID: {}", id);
        UserEntity existingUserEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для обновления",id);
                    return new EntityNotFoundException("User not found");
                });

        existingUserEntity.setName(userDto.getName());
        existingUserEntity.setEmail(userDto.getEmail());
        existingUserEntity.setAge(userDto.getAge());

        UserEntity updatedUserEntity = userRepository.save(existingUserEntity);
        log.info("Пользователь с ID: {} успешно обновлен",id);
        return userMapper.toDto(updatedUserEntity);
    }

    public void delete(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        UserEntity user = userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.deleteById(id);

        // Отправка события в Kafka
        UserEvent event = UserEvent.builder()
                .type(UserEventType.DELETED)
                .email(user.getEmail())
                .build();

        log.info("Отправка события удаления пользователя в Kafka. Email: {}, Топик: user-events", user.getEmail());
        CompletableFuture<SendResult<String, UserEvent>> future = kafkaTemplate.send("user-events", event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Событие DELETED для пользователя {} успешно отправлено в Kafka, topic = {}, partition = {}, offset = {}",
                        event.getEmail(), metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error("Ошибка при отправке события удаления пользователя в Kafka. Email: {}",
                        user.getEmail(), ex);
            }
        });

        log.info("Пользователь с ID: {} успешно удален", id);
    }

    public List<UserDto> findAll() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
