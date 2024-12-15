package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @RequestBody @Valid BookingDto requestDto) {

        log.info("Creating booking for userId={}, request={}", userId, requestDto);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> respondToBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                   @PathVariable @Positive Long bookingId,
                                                   @RequestParam Boolean approved) {

        log.info("Responding to bookingId={}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.respondToBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                 @PathVariable @Positive Long bookingId) {

        log.info("Get booking with bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {

        log.info("Get bookings with userId ={}", userId);
        return bookingClient.getBookings(userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {

        log.info("Get bookings by owner with userId ={}", userId);
        return bookingClient.getBookingsByOwner(userId, state);
    }
}