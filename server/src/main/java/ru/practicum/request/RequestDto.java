package ru.practicum.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created = LocalDateTime.now();
    private List<Response> items;
}
