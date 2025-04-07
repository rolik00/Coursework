package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.coursework.MinorsHSEFeedback.db.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.title FROM Category c WHERE c.id = :id")
    String findTitleById(@Param("id") Long id);
}
