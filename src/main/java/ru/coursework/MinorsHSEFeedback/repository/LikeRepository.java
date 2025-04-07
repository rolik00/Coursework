package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Like;

import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.reviewId = :reviewId")
    Set<Like> getLikes(@Param("reviewId") Long reviewId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.reviewId = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(1) FROM Like l where l.reviewId IN :reviewId AND l.value = true")
    int getCountLikesByReviewIds(@Param("reviewId") Set<Long> reviewId);

    @Query("SELECT COUNT(1) FROM Like l where l.reviewId IN :reviewId AND l.value = false")
    int getCountDislikesByReviewIds(@Param("reviewId") Set<Long> reviewId);

    @Query("SELECT l FROM Like l WHERE l.userId = :userId AND l.reviewId = :reviewId")
    Optional<Like> findLikeByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
}
