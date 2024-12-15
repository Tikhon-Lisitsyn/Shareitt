package ru.practicum.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.InvalidBookingException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void shouldApproveBookingSuccessfully() {
        User owner = createUser(1L, "Owner", "owner@example.com");
        User booker = createUser(2L, "Booker", "booker@example.com");
        Item item = createItem(1L, "Item", "Description", owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        Booking result = bookingService.respondToBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldRejectBookingSuccessfully() {
        User owner = createUser(1L, "Owner", "owner@example.com");
        User booker = createUser(2L, "Booker", "booker@example.com");
        Item item = createItem(1L, "Item", "Description", owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        Booking result = bookingService.respondToBooking(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionIfBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.respondToBooking(1L, 1L, true));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        User owner = createUser(1L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Item", "Description", owner);
        Booking booking = createBooking(1L, item, createUser(2L, "Booker", "booker@example.com"),
                BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.respondToBooking(1L, 1L, true));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserIsNotOwner() {
        User owner = createUser(1L, "Owner", "owner@example.com");
        User nonOwner = createUser(2L, "Non-Owner", "nonowner@example.com");
        Item item = createItem(1L, "Item", "Description", owner);
        Booking booking = createBooking(1L, item, createUser(3L, "Booker",
                "booker@example.com"), BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(nonOwner));

        InvalidBookingException exception = assertThrows(InvalidBookingException.class,
                () -> bookingService.respondToBooking(2L, 1L, true));
        assertEquals("Only the owner of the item can respond to the booking", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfBookingAlreadyResponded() {
        User owner = createUser(1L, "Owner", "owner@example.com");
        User booker = createUser(2L, "Booker", "booker@example.com");
        Item item = createItem(1L, "Item", "Description", owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        InvalidBookingException exception = assertThrows(InvalidBookingException.class,
                () -> bookingService.respondToBooking(1L, 1L, false));
        assertEquals("Booking has already been responded to", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfItemNotAvailable() {
        User user = createUser(1L, "User", "user@example.com");
        Item item = createItem(1L, "Item", "Description", user);
        item.setAvailable(false);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDto));
        assertEquals("Item is not available", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfItemNotFoundForBooking() {
        User user = createUser(1L, "User", "user@example.com");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto));
        assertEquals("Item not found", exception.getMessage());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(Long id, String name, String description, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        return item;
    }

    private Booking createBooking(Long id, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        return booking;
    }
}
