package ru.practicum.categories.mapper;

import ru.practicum.categories.model.Category;
import ru.practicum.categories.model.dto.CategoryDto;
import ru.practicum.categories.model.dto.NewCategoryDto;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static Category toCategoryNew(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}
