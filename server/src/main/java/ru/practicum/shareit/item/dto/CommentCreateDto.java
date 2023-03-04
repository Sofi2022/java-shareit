package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class CommentCreateDto {

    private String text;

    @CreationTimestamp
    private LocalDateTime created;

    private ItemDto item;

    private UserDto author;
}
