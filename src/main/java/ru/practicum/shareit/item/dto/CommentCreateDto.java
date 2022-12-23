package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class CommentCreateDto {

    @NotBlank
    private String text;

    @CreationTimestamp
    private LocalDateTime created;

    private Item item;

    private User author;
}
