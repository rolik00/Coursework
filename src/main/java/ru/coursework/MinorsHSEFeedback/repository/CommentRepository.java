package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.coursework.MinorsHSEFeedback.db.Comment;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.reviewId = :reviewId")
    Set<Comment> getComments(@Param("reviewId") Long reviewId);
}
