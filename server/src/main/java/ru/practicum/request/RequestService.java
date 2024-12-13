package ru.practicum.request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long userId, RequestDto requestDto);

    List<RequestDto> getOwnRequests(Long userId);

    List<RequestDto> getAllRequests();

    RequestDto getRequestById(Long requestId);
}
