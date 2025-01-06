package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    String findTitleById(Long id);
}
