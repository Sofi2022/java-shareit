package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ItemCreateRequest {

    @NotBlank
    private String description;

    private UserDto requester;

    @CreationTimestamp
    private LocalDateTime created;
}
