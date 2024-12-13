package ru.practicum.request;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                              ItemRepository itemRepository, ModelMapper modelMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Request createRequest(Long userId, RequestDto requestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Requestor not found"));
        Request request = toEntity(requestDto);
        request.setRequestor(requestor);
        requestRepository.save(request);
        return request;

    }

    @Override
    public List<RequestDto> getOwnRequests(Long userId) {
        List<Request> userRequests = requestRepository.findAllByRequestorId(userId);

        return userRequests.stream()
                .map(this::mapRequestToDtoWithResponses)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequests() {
        List<Request> requests = requestRepository.findAll();

        return requests.stream()
                .map(this::mapRequestToDtoWithResponses)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return mapRequestToDtoWithResponses(request);
    }

    private RequestDto mapRequestToDtoWithResponses(Request request) {
        List<Item> items = itemRepository.findAllByRequestId(request.getId());

        List<Response> responses = items.stream()
                .map(item -> new Response(
                        item.getId(),
                        item.getName(),
                        item.getOwner().getId()
                ))
                .collect(Collectors.toList());
        RequestDto requestDto = toRequestDto(request);
        requestDto.setItems(responses);
        return requestDto;
    }

    private Request toEntity(RequestDto requestDto) {
        return modelMapper.map(requestDto, Request.class);
    }

    private RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequestor(request.getRequestor().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setDescription(request.getDescription());
        return requestDto;
    }
}
