package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody @Valid BookingDto bookingRequest) {

        return bookingService.createBooking(userId, bookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public Booking respondToBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.respondToBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookings(userId);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}