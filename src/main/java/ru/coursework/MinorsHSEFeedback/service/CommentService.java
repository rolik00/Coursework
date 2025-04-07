package ru.coursework.MinorsHSEFeedback.service;

import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;

import java.util.Set;

public interface CommentService {
    Comment createComment(CreateCommentRequest request);
    Comment updateComment(UpdateCommentRequest request);
    boolean deleteComment(Long id, String email);
    Set<Comment> getComments(Long reviewId);
}
