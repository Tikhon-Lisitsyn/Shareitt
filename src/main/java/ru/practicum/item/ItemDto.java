package ru.practicum.item;

import lombok.Data;
import ru.practicum.booking.BookingDto;
import ru.practicum.user.User;

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
}