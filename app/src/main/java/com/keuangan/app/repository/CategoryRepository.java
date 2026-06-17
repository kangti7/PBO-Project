package com.keuangan.app.repository;

import com.keuangan.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Category> findAllByType(String type);
    Optional<Category> findByNameIgnoreCaseAndType(String name, String type);
}