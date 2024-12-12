package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping("/{itemId}")
    public Optional<ItemDto> get(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getOne(itemId, userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {
        return itemService.addNew(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId,
                       @RequestBody ItemDto itemDto) {

        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {

        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@PathVariable @Valid Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody CommentRequestDto commentRequest) {
        return itemService.comment(itemId, userId, commentRequest.getText());
    }

}