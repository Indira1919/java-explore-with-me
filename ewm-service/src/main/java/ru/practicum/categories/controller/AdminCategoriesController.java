package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.model.dto.CategoryDto;
import ru.practicum.categories.model.dto.NewCategoryDto;
import ru.practicum.categories.service.CategoriesService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoriesService categoriesService;

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        return categoriesService.updateCategory(catId, categoryDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoriesService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Integer catId) {
        categoriesService.deleteCategoryById(catId);
    }
}
