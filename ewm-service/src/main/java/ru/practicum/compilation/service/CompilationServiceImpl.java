package ru.practicum.compilation.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.AlreadyExistsException;
import ru.practicum.common.exception.BadArgumentsException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException, AlreadyExistsException {
        log.info("Создание новой подборки: {}", dto.getTitle());

        Compilation compilation = CompilationMapper.toCompilation(dto);

        if (compilationRepository.existsByTitle(compilation.getTitle())) {
            throw new AlreadyExistsException("Подборка с таким именем уже существует");
        }

        // Добавлять события в подборку, если они указаны
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            // Получаем коллекцию событий по списку идентификаторов из DTO
            List<Event> events = eventRepository.findAllById(dto.getEvents());

            // Формируем список идентификаторов событий, которые были в DTO, но не найдены в БД
            Set<Long> foundIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());

            List<Long> missedEventIds = dto.getEvents().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            // Если список не пустой
            if (!missedEventIds.isEmpty()) {
                throw new NotFoundException("Events with ids=" + missedEventIds + " was not found");
            }

            compilation.setEvents(new HashSet<>(events));
        }

        compilation = compilationRepository.save(compilation);
        log.info("Подборка создана с id: {}", compilation.getId());

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) throws NotFoundException {
        log.info("Удаление подборки с id: {}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }

        compilationRepository.deleteById(compId);
        log.info("Подборка с id {} удалена", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) throws NotFoundException, AlreadyExistsException {
        log.info("Обновление подборки с id: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        // Обновлять поля подборки, если они переданы
        if (dto.getTitle() != null) {
            if (compilationRepository.existsByTitle(dto.getTitle())) {
                throw new AlreadyExistsException("Подборка с таким именем уже существует");
            }
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            for (Long eventId : dto.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
                events.add(event);
            }
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);
        log.info("Подборка с id {} обновлена", compId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) throws NotFoundException {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id " + compId + " не найдена"));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) throws
                                                                                            BadArgumentsException {

        if (from < 0 || size <= 0) {
            throw new BadArgumentsException("Неверные параметры пагинации");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Compilation> page;
        if (pinned != null) {
            page = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            page = compilationRepository.findAll(pageable);
        }
        return page.getContent().stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }
}