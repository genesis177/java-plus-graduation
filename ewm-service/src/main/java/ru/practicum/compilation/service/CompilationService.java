package ru.practicum.compilation.service;

import org.apache.coyote.BadRequestException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException;

    void deleteCompilation(Long compId) throws NotFoundException;

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) throws NotFoundException;

    CompilationDto getCompilationById(Long compId) throws NotFoundException;

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) throws BadRequestException;
}