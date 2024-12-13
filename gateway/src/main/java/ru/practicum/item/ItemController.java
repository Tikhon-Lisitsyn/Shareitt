package ru.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @PathVariable @Positive Long itemId,
                                         @RequestBody @Valid ItemDto itemDto) {

        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam @NotBlank String text) {

        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@PathVariable @Positive Long itemId,
                              @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                              @RequestBody @Valid CommentRequestDto commentRequest) {
        return itemClient.comment(itemId, userId, commentRequest);
    }
}
