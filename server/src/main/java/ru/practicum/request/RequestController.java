package ru.practicum.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class RequestController {
    private final RequestServiceImpl requestService;

    @PostMapping
    public Request createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody RequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }
}
