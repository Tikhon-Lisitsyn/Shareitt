package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.InvalidBookingException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Booking createBooking(Long userId, BookingDto bookingRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    public Booking respondToBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Item item = booking.getItem();

        if (!item.getOwner().equals(user)) {
            throw new InvalidBookingException("Only the owner of the item can respond to the booking");
        }

        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new InvalidBookingException("Booking has already been responded to");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return booking;
    }

    public Booking getBookingInfo(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Item item = booking.getItem();
        if (!(booking.getBooker().equals(user) || item.getOwner().equals(user))) {
            throw new IllegalArgumentException("Only the owner and the booker of the item can respond to the booking");
        }

        return booking;
    }

    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findBookingsByUserId(userId);
    }

    public List<Booking> getBookingsByOwner(Long userId, String state) {
        BookingStatus bookingStatus = BookingStatus.valueOf(state);

        if (state.equals("ALL")) {
            return bookingRepository.findBookingsByOwner(userId);
        } else {
            return bookingRepository.findBookingsByOwnerAndStatus(userId, bookingStatus);
        }
    }
}