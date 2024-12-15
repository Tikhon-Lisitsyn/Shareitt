package ru.practicum.user;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;


@Service
public interface UserService {
    UserDto getUser(Long userId) throws BadRequestException;

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto) throws BadRequestException;

    User removeUser(Long userId) throws BadRequestException;
}