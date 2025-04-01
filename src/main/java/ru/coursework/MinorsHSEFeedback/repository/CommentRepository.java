package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
