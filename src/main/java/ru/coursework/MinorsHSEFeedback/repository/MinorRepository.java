package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Minor;

import java.util.List;

public interface MinorRepository extends JpaRepository<Minor, Long> {
    List<Minor> findAllByCategoryId(Long categoryId);
}
