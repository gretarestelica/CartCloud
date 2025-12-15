package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.model.Category;
import com.cartcloud.cartcloud.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryRepository.save(category);
}

}

