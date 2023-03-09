package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
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

    private LocalDateTime created;
}
