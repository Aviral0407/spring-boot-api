package com.aviral.spring_boot_api.repository;

import com.aviral.spring_boot_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByMonthId(Long monthId);
}