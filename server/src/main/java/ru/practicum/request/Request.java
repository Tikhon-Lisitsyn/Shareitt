package ru.practicum.request;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;
    private LocalDateTime created;
}