package ru.practicum.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDto {
    private Long id;
    private String description;
    @Positive(message = "Requestor id must be a positive number")
    private Long requestor;
    private LocalDateTime created = LocalDateTime.now();
    private List<Response> items;
}
