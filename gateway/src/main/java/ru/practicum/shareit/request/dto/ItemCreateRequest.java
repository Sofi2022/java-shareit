package ru.practicum.shareit.request.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ItemCreateRequest {

    @NotNull
    private String description;

    private UserDto requester;

    @CreationTimestamp
    private LocalDateTime created;
}
