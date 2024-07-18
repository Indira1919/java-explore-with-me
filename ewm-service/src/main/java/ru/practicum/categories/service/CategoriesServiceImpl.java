package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.model.dto.CategoryDto;
import ru.practicum.categories.model.dto.NewCategoryDto;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return categoriesRepository.findAll(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория не найдена или недоступна"));

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        categoriesRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория не найдена или недоступна"));

        categoryDto.setId(catId);

        return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {

        return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategoryNew(newCategoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategoryById(Integer catId) {
        categoriesRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория не найдена или недоступна"));

        categoriesRepository.deleteById(catId);
    }
}
