package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Valid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class CommentCreateDto {

    @NotBlank
    private String text;

    private LocalDateTime created;

    private ItemDto item;

    private UserDto author;
}
