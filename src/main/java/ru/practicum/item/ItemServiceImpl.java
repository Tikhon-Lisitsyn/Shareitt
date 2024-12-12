package ru.practicum.item;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.BookingStatus;
import ru.practicum.exception.InvalidBookingException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addNew(Long userId, ItemDto itemDto) {
        Item item = toItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (item.getAvailable() == null) {
            throw new ValidationException("Available cannot be null");
        }

        if (item.getName().isEmpty()) {
            throw new ValidationException("Name cannot be null");
        }

        item.setOwner(user);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        itemDto.setOwner(user);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemDto.getId()));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner of this item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(existingItem);
        return toItemDto(existingItem);
    }

    @Override
    public Optional<ItemDto> getOne(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));

        ItemDto itemDto = toItemDto(item);

        addBookingsToItem(itemDto, itemId, userId);

        return Optional.of(itemDto);
    }

    @Override
    public List<Item> getAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return itemRepository.findAllByOwner(user);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemByText(text);
    }

    public CommentDto comment(Long itemId, Long userId, String text) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean hasBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndDateBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new InvalidBookingException("User has not booked this item or booking not completed");
        }

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setText(text);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return toCommentDto(comment);
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    private Item toItem(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }

    private void addBookingsToItem(ItemDto itemDto, Long itemId, Long userId) {

        Booking lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (lastBooking != null && Objects.equals(lastBooking.getItem().getOwner().getId(), userId)) {
            itemDto.setLastBooking(toBookingDto(lastBooking));
        }

        if (nextBooking != null && Objects.equals(nextBooking.getItem().getOwner().getId(), userId)) {
            itemDto.setNextBooking(toBookingDto(nextBooking));
        }
    }

    private BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }

    private ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwner(item.getOwner());
        itemDto.setComments(item.getComments());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

}