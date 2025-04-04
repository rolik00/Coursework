package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.Review;

import java.util.Optional;
import java.util.Set;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findById(Long id);

    @Query("SELECT r FROM Review r WHERE r.minorId = :minorId")
    Set<Review> getReviews(@Param("minorId") Long minorId);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.minorId = :minorId")
    Optional<Review> findReviewByUserIdAndMinorId(@Param("userId") Long userId, @Param("minorId") Long minorId);
}
