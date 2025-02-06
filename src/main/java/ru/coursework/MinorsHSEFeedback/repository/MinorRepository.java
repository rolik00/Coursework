package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.coursework.MinorsHSEFeedback.db.Minor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MinorRepository extends JpaRepository<Minor, Long> {
    @Override
    Optional<Minor> findById(Long id);

    @Query("SELECT m FROM Minor m WHERE m.categoryId IN :categoryIds")
    List<Minor> findAllByCategoryIds(@Param("categoryIds") Set<Long> categoryIds);
}
