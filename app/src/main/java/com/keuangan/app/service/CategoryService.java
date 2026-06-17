package com.keuangan.app.service;

import com.keuangan.app.dto.CategoryRequest;
import com.keuangan.app.model.Category;
import com.keuangan.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getCategories(String type) {
        if (type != null && !type.trim().isEmpty()) {
            return categoryRepository.findAllByType(type.toUpperCase());
        }
        return categoryRepository.findAll();
    }

    public Category createCategory(CategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.getName().trim())) {
            throw new IllegalArgumentException("Nama kategori '" + req.getName() + "' sudah digunakan");
        }
        Category category = new Category(req.getName().trim(), req.getType().toUpperCase());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryRequest req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan"));
        
        if (!category.getName().equalsIgnoreCase(req.getName()) && categoryRepository.existsByNameIgnoreCase(req.getName())) {
            throw new IllegalArgumentException("Nama kategori sudah digunakan");
        }
        
        category.setName(req.getName().trim());
        category.setType(req.getType().toUpperCase());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Kategori tidak ditemukan");
        }
        categoryRepository.deleteById(id);
    }
}