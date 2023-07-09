package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(ItemRequestDto dto, Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    List<ItemRequestDto> getAllRequestsPageable(Long userId, Integer from, Integer size);

    ItemRequestDto getRequest(Long requestId, Long userId);
}
