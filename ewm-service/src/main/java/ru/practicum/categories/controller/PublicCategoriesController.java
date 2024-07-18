package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.model.dto.CategoryDto;
import ru.practicum.categories.service.CategoriesService;

import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return categoriesService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Integer catId) {
        return categoriesService.getCategoryById(catId);
    }
}
