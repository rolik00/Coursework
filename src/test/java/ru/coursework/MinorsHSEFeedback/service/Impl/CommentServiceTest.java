package ru.coursework.MinorsHSEFeedback.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.CommentException;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private CommentServiceImpl commentService;
    private User user;
    private Review review;
    private Comment comment;
    private Comment parentComment;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        review = new Review();
        review.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setReviewId(review.getId());
        comment.setUserId(user.getId());
        comment.setBody("Test comment");
        comment.setCreateDate(LocalDate.now());
        comment.setParentId(2L);

        parentComment = new Comment();
        parentComment.setId(2L);
        parentComment.setReviewId(review.getId());
        parentComment.setUserId(user.getId());
        parentComment.setBody("Test parent comment");
        parentComment.setCreateDate(LocalDate.now());
    }

    @Test
    @DisplayName("Успешное создание комментария")
    public void testCreateComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(review.getId());
        request.setBody("Test comment");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.createComment(request);
        assertNotNull(createdComment);
        assertEquals(comment.getBody(), createdComment.getBody());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository, never()).findById(request.getParentId());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не создан, так как пользователь не найден")
    public void testCreateCommentUserNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail("nonexistent@example.com");
        request.setReviewId(review.getId());
        request.setBody("Test comment");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> commentService.createComment(request));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository, never()).findById(request.getParentId());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не создан, так как отзыв не найден")
    public void testCreateCommentReviewNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(2L);
        request.setBody("Test comment");

        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class, () -> commentService.createComment(request));
        assertEquals("Отзыв не найден", exception.getMessage());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository, never()).findById(request.getParentId());
        verify(userRepository, never()).findByEmail(request.getEmail());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не создан, так как родительский комментарий не найден")
    public void testCreateCommentParentCommentNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(review.getId());
        request.setBody("Test comment");
        request.setParentId(5L);

        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(commentRepository.findById(request.getParentId())).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class, () -> commentService.createComment(request));
        assertEquals("Комментарий, на который хотим ответить не найден", exception.getMessage());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository).findById(request.getParentId());
        verify(userRepository, never()).findByEmail(request.getEmail());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не создан, так как родительский комментарий не принадлежит текущему отзыву")
    public void testCreateCommentParentCommentNotApplyToReview() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(review.getId());
        request.setBody("Test comment");
        request.setParentId(parentComment.getId());

        parentComment.setReviewId(2L);

        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(commentRepository.findById(request.getParentId())).thenReturn(Optional.of(parentComment));

        CommentException exception = assertThrows(CommentException.class, () -> commentService.createComment(request));
        assertEquals("Комментарий, на который отвечаем не пренадлежит текущему отзыву", exception.getMessage());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository).findById(request.getParentId());
        verify(userRepository, never()).findByEmail(request.getEmail());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Успешное создание ответного комментария")
    public void testCreateCommentFromParentComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(review.getId());
        request.setBody("Test comment");
        request.setParentId(parentComment.getId());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(commentRepository.findById(request.getParentId())).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.createComment(request);
        assertNotNull(createdComment);
        assertEquals(comment.getBody(), createdComment.getBody());
        verify(reviewRepository).findById(request.getReviewId());
        verify(commentRepository).findById(request.getParentId());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Успешное обновление комментария")
    public void testUpdateComment() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(comment.getId());
        request.setEmail(user.getEmail());
        request.setBody("Updated comment");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(request.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment updatedComment = commentService.updateComment(request);
        assertNotNull(updatedComment);
        assertEquals(request.getBody(), updatedComment.getBody());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository, times(2)).findById(request.getId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не обновлен, так как пользователь не найден")
    public void testUpdateCommentUserNotFound() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(comment.getId());
        request.setEmail("nonexistent@example.com");
        request.setBody("Updated comment");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> commentService.updateComment(request));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository, never()).findById(request.getId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не обновлен, так как не был найден")
    public void testUpdateCommentNotFound() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(2L);
        request.setEmail(user.getEmail());
        request.setBody("Updated comment");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(request.getId())).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class, () -> commentService.updateComment(request));
        assertEquals("Комментарий не найден", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository).findById(request.getId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не обновлен, так как пользователь не является создателем")
    public void testUpdateCommentUserNotCreator() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setId(comment.getId());
        request.setEmail(user.getEmail());
        request.setBody("Updated comment");
        user.setId(2L);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(request.getId())).thenReturn(Optional.of(comment));

        CommentException exception = assertThrows(CommentException.class, () -> commentService.updateComment(request));
        assertEquals("Текущий пользователь не является создателем данного комментария", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(commentRepository).findById(request.getId());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Успешное удаление комментария")
    public void testDeleteComment() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(comment.getId(), user.getEmail());

        assertTrue(result);
        verify(userRepository).findByEmail(user.getEmail());
        verify(commentRepository, times(2)).findById(comment.getId());
        verify(commentRepository).delete(comment);
        verify(commentRepository).updateComment(comment.getId(), comment.getParentId());
    }

    @Test
    @DisplayName("Комментарий не удален, так как пользователь не найден")
    public void testDeleteCommentUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> commentService.deleteComment(comment.getId(), email));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(commentRepository, never()).findById(comment.getId());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не удален, так как не был найден")
    public void testDeleteCommentNotFound() {
        Long id = 2L;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        CommentException exception = assertThrows(CommentException.class, () -> commentService.deleteComment(id, user.getEmail()));
        assertEquals("Комментарий не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(commentRepository).findById(id);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарий не удален, так как пользователь не является создателем")
    public void testDeleteCommentUserNotCreator() {
        user.setId(2L);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        CommentException exception =
                assertThrows(CommentException.class, () -> commentService.deleteComment(comment.getId(), user.getEmail()));
        assertEquals("Текущий пользователь не является создателем данного комментария", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(commentRepository).findById(comment.getId());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Успешное получение комментариев к отзыву")
    public void testGetComments() {
        when(commentRepository.getComments(review.getId())).thenReturn(Set.of(comment));

        Set<Comment> comments = commentService.getComments(review.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertTrue(comments.contains(comment));
    }
}