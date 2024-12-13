package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void shouldCreateRequestSuccessfully() {
        UserDto user = userService.addUser(createUserDto(null, "Test User", "testuser@example.com"));
        RequestDto requestDto = createRequestDto(null, "Need a hammer");

        Request createdRequest = requestService.createRequest(user.getId(), requestDto);

        assertNotNull(createdRequest.getId(), "Created request should have an ID");
        assertEquals("Need a hammer", createdRequest.getDescription(), "Description should match");
        assertEquals(user.getId(), createdRequest.getRequestor().getId(), "Requestor ID should match");
    }

    @Test
    void shouldGetOwnRequestsSuccessfully() {
        UserDto user = userService.addUser(createUserDto(null, "Test User", "testuser@example.com"));
        RequestDto requestDto1 = createRequestDto(null, "Need a hammer");
        RequestDto requestDto2 = createRequestDto(null, "Looking for a wrench");

        requestService.createRequest(user.getId(), requestDto1);
        requestService.createRequest(user.getId(), requestDto2);

        List<RequestDto> ownRequests = requestService.getOwnRequests(user.getId());

        assertEquals(2, ownRequests.size(), "User should have 2 requests");
    }

    @Test
    void shouldGetRequestByIdSuccessfully() {
        UserDto user = userService.addUser(createUserDto(null, "Test User", "testuser@example.com"));
        RequestDto requestDto = createRequestDto(null, "Need a drill");

        Request createdRequest = requestService.createRequest(user.getId(), requestDto);

        RequestDto fetchedRequest = requestService.getRequestById(createdRequest.getId());

        assertEquals(createdRequest.getId(), fetchedRequest.getId(), "Request ID should match");
        assertEquals("Need a drill", fetchedRequest.getDescription(), "Description should match");
    }

    private RequestDto createRequestDto(Long id, String description) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setDescription(description);
        return requestDto;
    }

    private UserDto createUserDto(Long id, String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }
}
