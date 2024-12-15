package ru.practicum.item;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ItemService {

    ItemDto addNew(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto) throws BadRequestException;

    Optional<ItemDto> getOne(Long itemId, Long userId) throws BadRequestException;

    List<Item> getAll(Long userId) throws BadRequestException;

    List<Item> search(String text) throws BadRequestException;
}