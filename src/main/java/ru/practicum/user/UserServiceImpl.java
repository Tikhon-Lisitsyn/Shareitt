package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;


@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final ModelMapper modelMapper;

    public UserDto getUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return toUserDto(user);
    }

    public UserDto addUser(UserDto userDto) {
        User user = toUser(userDto);
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationException("Почта или имя пользователя не может быть null");
        }

        if (repository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("User with this email already exists");
        }

        repository.save(user);

        return toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                    repository.existsByEmail(userDto.getEmail())) {
                throw new EmailAlreadyExistsException("User with this email already exists");
            }
            existingUser.setEmail(userDto.getEmail());
        }
        repository.save(existingUser);
        return toUserDto(existingUser);
    }

    public void removeUser(Long userId) {
        repository.deleteById(userId);
    }

    private User toUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}