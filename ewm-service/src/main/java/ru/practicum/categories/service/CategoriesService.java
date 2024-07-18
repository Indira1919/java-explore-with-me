package ru.practicum.categories.service;

import ru.practicum.categories.model.dto.CategoryDto;
import ru.practicum.categories.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoriesService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);

    CategoryDto updateCategory(Integer catId, CategoryDto categoryDto);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Integer catId);
}
