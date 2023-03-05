package ru.practicum.shareit.item.dto;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
//import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.Valid;
//import javax.validation.constraints.NotBlank;
//import java.util.Set;

@Valid
@Data
@RequiredArgsConstructor
public class ItemResponseWithBooking {

//    private long id;
//
//    @NotBlank
//    private String name;
//
//    @NotBlank
//    private String description;
//
//    private Boolean available;
//
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    private ShortBookingDto lastBooking;
//
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    private ShortBookingDto nextBooking;
//
//    private Set<CommentResponseDto> comments;
}
