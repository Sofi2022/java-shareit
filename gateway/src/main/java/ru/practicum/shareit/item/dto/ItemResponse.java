package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Valid
@Data
@AllArgsConstructor
public class ItemResponse {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Boolean available;

    private Long requestId;
}
