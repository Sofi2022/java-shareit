package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ItemDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long ownerId;
}
