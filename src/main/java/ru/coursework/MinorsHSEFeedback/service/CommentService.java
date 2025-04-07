package ru.coursework.MinorsHSEFeedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.exceptions.CommentException;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.mapper.UiCommentMapper;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UiCommentMapper commentMapper;
    @Transactional
    public UiComment createComment(CreateCommentRequest request) {
        checkCanCreateComment(request.getParentId(), request.getReviewId());
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Comment comment = new Comment();
        comment.setReviewId(request.getReviewId());
        comment.setUserId(user.getId());
        comment.setBody(request.getBody());
        comment.setCreateDate(LocalDate.now());
        comment.setParentId(request.getParentId());
        commentRepository.save(comment);
        return commentMapper.apply(comment);
    }

    @Transactional
    public UiComment updateComment(UpdateCommentRequest request) {
        checkCanUpdateOrDeleteComment(request.getId(), request.getEmail());
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new CommentException("Comment not found"));
        comment.setBody(request.getBody());
        commentRepository.save(comment);
        return commentMapper.apply(comment);
    }

    @Transactional
    public boolean deleteComment(Long id, String email) {
        checkCanUpdateOrDeleteComment(id, email);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Comment not found"));
        commentRepository.delete(comment);
        commentRepository.updateComment(comment.getId(), comment.getParentId());
        return true;
    }

    @Transactional(readOnly = true)
    public Set<UiComment> getComments(Long reviewId) {
        Set<Comment> comments = commentRepository.getComments(reviewId);
        return comments.stream().map(commentMapper::apply).collect(Collectors.toSet());
    }

    private void checkCanCreateComment(Long parentId, Long reviewId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException("Parent comment not found"));
        if (!parentComment.getReviewId().equals(reviewId)) {
            throw new CommentException("Комментарий, на который отвечаем не пренадлежит текущему отзыву");
        }
    }

    private void checkCanUpdateOrDeleteComment(Long id, String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Comment not found"));
        if (!comment.getUserId().equals(user.getId())) {
            throw new CommentException("Текущий пользователь не является создателем данного комментария");
        }
    }
}
