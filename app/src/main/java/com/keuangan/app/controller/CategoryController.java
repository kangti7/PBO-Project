package com.keuangan.app.controller;

import com.keuangan.app.dto.CategoryRequest;
import com.keuangan.app.model.Category;
import com.keuangan.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(categoryService.getCategories(type));
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editCategory(@PathVariable Long id, @RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Kategori berhasil dihapus");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}