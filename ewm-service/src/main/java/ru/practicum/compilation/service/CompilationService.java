package ru.practicum.compilation.service;

import java.util.List;
import ru.practicum.common.exception.BadArgumentsException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException;

    void deleteCompilation(Long compId) throws NotFoundException;

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) throws NotFoundException;

    CompilationDto getCompilationById(Long compId) throws NotFoundException;

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) throws BadArgumentsException;
}