package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return userServiceImpl.getUser(userId);
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return userServiceImpl.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return userServiceImpl.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public User remove(@PathVariable Long userId) {
        log.info("Received DELETE request for user with id: {}", userId);
        return userServiceImpl.removeUser(userId);
    }
}
