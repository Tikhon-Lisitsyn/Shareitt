package ru.practicum.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    private RequestServiceImpl requestService;

    @BeforeEach
    void setup() {
        requestService = new RequestServiceImpl(requestRepository, userRepository, itemRepository, modelMapper);
    }

    @Test
    void shouldCreateRequestSuccessfully() {
        Long userId = 1L;
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Test description");

        User user = new User();
        user.setId(userId);
        Request request = new Request();
        request.setDescription("Test description");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(requestDto, Request.class)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);

        Request result = requestService.createRequest(userId, requestDto);

        assertEquals("Test description", result.getDescription());
        assertEquals(user, result.getRequestor());
        verify(requestRepository).save(request);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundOnCreate() {
        Long userId = 1L;
        RequestDto requestDto = new RequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(userId, requestDto));
    }

    @Test
    void shouldGetOwnRequestsSuccessfully() {
        Long userId = 1L;

        User requestor = new User();
        requestor.setId(userId);

        Request request1 = new Request();
        request1.setId(1L);
        request1.setRequestor(requestor);

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequestor(requestor);

        List<Request> userRequests = List.of(request1, request2);

        when(requestRepository.findAllByRequestorId(userId)).thenReturn(userRequests);

        List<RequestDto> result = requestService.getOwnRequests(userId);

        assertEquals(2, result.size());
        verify(requestRepository).findAllByRequestorId(userId);
    }

    @Test
    void shouldGetAllRequestsSuccessfully() {
        Long userId = 1L;

        User requestor = new User();
        requestor.setId(userId);

        Request request1 = new Request();
        request1.setId(1L);
        request1.setRequestor(requestor);

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequestor(requestor);

        List<Request> requests = List.of(request1, request2);

        when(requestRepository.findAll()).thenReturn(requests);

        List<RequestDto> result = requestService.getAllRequests();

        assertEquals(2, result.size());
        verify(requestRepository).findAll();
    }

    @Test
    void shouldGetRequestByIdSuccessfully() {
        Long requestId = 1L;
        Long userId = 1L;

        User requestor = new User();
        requestor.setId(userId);

        Request request = new Request();
        request.setId(requestId);
        request.setRequestor(requestor);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of());

        RequestDto result = requestService.getRequestById(requestId);

        assertEquals(requestId, result.getId());
        verify(requestRepository).findById(requestId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long requestId = 1L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(requestId));
    }
}
