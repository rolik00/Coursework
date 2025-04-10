package ru.coursework.MinorsHSEFeedback.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.CommentException;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;
import ru.coursework.MinorsHSEFeedback.service.CommentService;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Comment createComment(CreateCommentRequest request) {
        checkCanCreateComment(request.getParentId(), request.getReviewId());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Comment comment = new Comment();
        comment.setReviewId(request.getReviewId());
        comment.setUserId(user.getId());
        comment.setBody(request.getBody());
        comment.setCreateDate(LocalDate.now());
        comment.setParentId(request.getParentId());
        commentRepository.save(comment);
        return comment;
    }

    @Override
    @Transactional
    public Comment updateComment(UpdateCommentRequest request) {
        checkCanUpdateOrDeleteComment(request.getId(), request.getEmail());
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new CommentException("Комментарий не найден"));
        comment.setBody(request.getBody());
        commentRepository.save(comment);
        return comment;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id, String email) {
        checkCanUpdateOrDeleteComment(id, email);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Комментарий не найден"));
        commentRepository.delete(comment);
        commentRepository.updateComment(comment.getId(), comment.getParentId());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Comment> getComments(Long reviewId) {
        return commentRepository.getComments(reviewId);
    }

    private void checkCanCreateComment(Long parentId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommentException("Отзыв не найден"));
        if (parentId == null || parentId == 0) {
            return;
        }
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException("Комментарий, на который хотим ответить не найден"));
        if (!parentComment.getReviewId().equals(reviewId)) {
            throw new CommentException("Комментарий, на который отвечаем не пренадлежит текущему отзыву");
        }
    }

    private void checkCanUpdateOrDeleteComment(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Комментарий не найден"));
        if (!comment.getUserId().equals(user.getId())) {
            throw new CommentException("Текущий пользователь не является создателем данного комментария");
        }
    }
}
