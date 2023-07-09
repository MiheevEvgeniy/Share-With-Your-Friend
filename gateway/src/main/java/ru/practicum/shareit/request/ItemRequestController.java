package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestDto dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("addRequest started: dto {}, userId {}", dto, userId);
        ResponseEntity<Object> itemRequestDto = client.addRequest(dto, userId);
        log.info("addRequest finished");
        return itemRequestDto;
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllRequests: userId {}", userId);
        ResponseEntity<Object> itemRequestDtos = client.getAllRequests(userId);
        log.info("getAllRequests finished");
        return itemRequestDtos;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsPageable(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("getAllRequestsPageable started: userId {}, from {}, size {}", userId, from, size);
        ResponseEntity<Object> itemRequestDtos = client.getAllRequestsPageable(userId, from, size);
        log.info("getAllRequestsPageable finished");
        return itemRequestDtos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@PathVariable("id") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getRequest started: requestId {}, userId {}", requestId, userId);
        ResponseEntity<Object> itemRequestDto = client.getRequest(requestId, userId);
        log.info("getRequest finished");
        return itemRequestDto;
    }
}
