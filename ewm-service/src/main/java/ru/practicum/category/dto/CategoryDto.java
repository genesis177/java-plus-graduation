package ru.practicum.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CategoryDto {
    private Long id;
    private String name;
}