package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addRequest(ItemRequestDto dto, Long userId) {
        dto.setCreated(LocalDateTime.now());
        return mapper.toDto(repository.save(mapper.toEntity(dto, checkUserId(userId))));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        List<ItemRequestDto> itemRequestDtos = repository.findAllByOwnerOrderByCreatedDesc(checkUserId(userId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        itemRequestDtos.forEach(itemRequestDto -> itemRequestDto
                .setItems(itemRepository.findAllByRequestId(
                                itemRequestDto.getId())
                        .stream()
                        .map(itemMapper::toDtoForRequest)
                        .collect(Collectors.toList())));
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequestsPageable(Long userId, Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from, size, sortById);

        List<ItemRequestDto> itemRequestDtos = repository.findAll(checkUserId(userId), page).stream().map(mapper::toDto).collect(Collectors.toList());
        itemRequestDtos.forEach(itemRequestDto -> itemRequestDto
                .setItems(itemRepository.findAllByRequestId(
                                itemRequestDto.getId())
                        .stream()
                        .map(itemMapper::toDtoForRequest)
                        .collect(Collectors.toList())));
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        Optional<ItemRequest> itemRequest = repository.findById(requestId);
        if (itemRequest.isPresent() && checkUserId(userId) != null) {
            ItemRequestDto itemRequestDto = mapper.toDto(itemRequest.get());
            itemRequestDto.setItems(itemRepository.findAllByRequestId(
                            itemRequestDto.getId())
                    .stream()
                    .map(itemMapper::toDtoForRequest)
                    .collect(Collectors.toList()));
            return itemRequestDto;
        } else {
            throw new ItemRequestNotFoundException("Запрос не найден");
        }
    }

    private User checkUserId(Long userId) {
        try {
            return userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        } catch (EntityNotFoundException e) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
    }
}
