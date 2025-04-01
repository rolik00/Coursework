package ru.coursework.MinorsHSEFeedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.exceptions.CommentException;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    @Transactional
    public UiComment createReview(CreateCommentRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Comment comment = new Comment();
        comment.setReviewId(request.getReviewId());
        comment.setUserId(user.getId());
        comment.setBody(request.getBody());
        comment.setCreateDate(LocalDate.now());
        commentRepository.save(comment);
        //пересчет value для отзыва
        return new UiComment(comment.getId(), comment.getBody(), comment.getReviewId(), user.getName(), comment.getCreateDate());
    }

    @Transactional
    public UiComment updateReview(UpdateCommentRequest request) {
        checkCanUpdateOrDeleteReview(request.getId(), request.getEmail());
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new CommentException("Comment not found"));
        comment.setBody(request.getBody());
        commentRepository.save(comment);
        String userName = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")).getName();
        //пересчет value для отзыва
        return new UiComment(comment.getId(), comment.getBody(), comment.getReviewId(), userName, comment.getCreateDate());
    }

    @Transactional
    public boolean deleteComment(Long id, String email) {
        checkCanUpdateOrDeleteReview(id, email);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Comment not found"));
        commentRepository.delete(comment);
        //пересчет value для отзыва
        return true;
    }

    private void checkCanUpdateOrDeleteReview(Long id, String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException("Comment not found"));
        if(!comment.getUserId().equals(user.getId())) {
            throw new CommentException("Текущий пользователь не является создателем данного комментария");
        }
    }
}
