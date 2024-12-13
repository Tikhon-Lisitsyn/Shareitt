package ru.practicum.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
