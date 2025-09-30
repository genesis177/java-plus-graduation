package ru.practicum.compilation.service;


import org.springframework.data.domain.Page;
import ru.practicum.common.exception.AlreadyExistsException;
import ru.practicum.common.exception.BadArgumentsException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException, AlreadyExistsException;

    void deleteCompilation(Long compId) throws NotFoundException;

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) throws NotFoundException, AlreadyExistsException;

    CompilationDto getCompilationById(Long compId) throws NotFoundException;

    Page<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) throws BadArgumentsException;
}