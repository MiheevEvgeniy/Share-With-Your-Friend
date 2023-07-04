package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("addRequest started: dto {}, userId {}", dto, userId);
        ItemRequestDto itemRequestDto = service.addRequest(dto, userId);
        log.info("addRequest finished");
        return itemRequestDto;
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllRequests: userId {}", userId);
        List<ItemRequestDto> itemRequestDtos = service.getAllRequests(userId);
        log.info("getAllRequests finished");
        return itemRequestDtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsPageable(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0")
                                                       @Min(0)
                                                       Integer from,
                                                       @RequestParam(defaultValue = "10")
                                                       @Min(1)
                                                       Integer size) {
        log.info("getAllRequestsPageable started: userId {}, from {}, size {}", userId, from, size);
        List<ItemRequestDto> itemRequestDtos = service.getAllRequestsPageable(userId, from, size);
        log.info("getAllRequestsPageable finished");
        return itemRequestDtos;
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable("id") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getRequest started: requestId {}, userId {}", requestId, userId);
        ItemRequestDto itemRequestDto = service.getRequest(requestId, userId);
        log.info("getRequest finished");
        return itemRequestDto;
    }
}
