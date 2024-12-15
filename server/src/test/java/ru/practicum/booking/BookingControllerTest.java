package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;
    private Long userId = 1L;
    private Long bookingId = 100L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testRespondToBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.respondToBooking(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).respondToBooking(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    void testGetBookingInfo() throws Exception {
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingService.getBookingInfo(eq(userId), eq(bookingId))).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService, times(1)).getBookingInfo(eq(userId), eq(bookingId));
    }

    @Test
    void testGetBookings() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(bookingId);
        Booking booking2 = new Booking();
        booking2.setId(bookingId + 1);

        when(bookingService.getBookings(eq(userId))).thenReturn(Arrays.asList(booking1, booking2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[1].id").value(bookingId + 1));

        verify(bookingService, times(1)).getBookings(eq(userId));
    }

    @Test
    void testGetBookingsByOwner() throws Exception {
        Booking booking1 = new Booking();
        booking1.setId(bookingId);
        Booking booking2 = new Booking();
        booking2.setId(bookingId + 1);

        when(bookingService.getBookingsByOwner(eq(userId), eq("ALL")))
                .thenReturn(Arrays.asList(booking1, booking2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[1].id").value(bookingId + 1));

        verify(bookingService, times(1)).getBookingsByOwner(eq(userId), eq("ALL"));
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
