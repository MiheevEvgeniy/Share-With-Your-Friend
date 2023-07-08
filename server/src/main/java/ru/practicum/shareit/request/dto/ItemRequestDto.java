package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
    @Builder.Default
    private List<ItemDtoForRequest> items = new ArrayList<>();

}
