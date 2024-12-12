package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) throws BadRequestException {
        return userServiceImpl.getUser(userId);
    }

    @PostMapping
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        return userServiceImpl.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                       @RequestBody UserDto userDto) {
        return userServiceImpl.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable Long userId) {
        userServiceImpl.removeUser(userId);
    }
}