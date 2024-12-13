package ru.practicum.dto;

import lombok.Data;
import ru.practicum.models.Comment;
import ru.practicum.models.User;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private List<Comment> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Long requestId;
}