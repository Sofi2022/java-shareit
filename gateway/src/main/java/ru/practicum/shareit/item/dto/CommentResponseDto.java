package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Valid
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Jacksonized
public class CommentResponseDto {

    @NotNull
    private long id;

    @NotBlank
    @NotNull
    private String text;

    @NotBlank
    private String authorName;

    @CreationTimestamp
    @NotNull
    private LocalDateTime created;
}
