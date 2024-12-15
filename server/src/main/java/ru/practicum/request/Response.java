package ru.practicum.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Response {
    private Long itemId;
    @JsonProperty("name")
    private String name;
    private Long ownerId;

    public Response(Long itemId, String name, Long ownerId) {
        this.itemId = itemId;
        this.name = name;
        this.ownerId = ownerId;
    }
}
