package ru.practicum.shareit.item.dto;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;

//import javax.validation.Valid;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

//@Valid
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Jacksonized
public class CommentResponseDto {

    @NotNull
    private long id;

    //@NotBlank
    @NotNull
    private String text;

    //@NotBlank
    private String authorName;

    @CreationTimestamp
    private LocalDateTime created;
}
