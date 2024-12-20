package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId) {

        return get("", userId);
    }

    public ResponseEntity<Object> bookItem(long userId, BookingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> respondToBooking(long userId, long bookingId, boolean approved) {
        return patch(String.format("/%d?approved=%b", bookingId, approved), userId, null, null);
    }

    public ResponseEntity<Object> getBookingInfo(long userId, long bookingId) {

        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByOwner(long userId, String state) {

        Map<String, Object> parameters = Map.of("state", state);
        return get("/" + userId, userId, parameters);
    }

}