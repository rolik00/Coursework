package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Comment;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.reviewId = :reviewId")
    Set<Comment> getComments(@Param("reviewId") Long reviewId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.reviewId = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(1) FROM Comment c where c.reviewId IN :reviewId")
    int countCommentsByReviewIds(@Param("reviewId") Set<Long> reviewId);
}
