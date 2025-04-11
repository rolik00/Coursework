package ru.coursework.MinorsHSEFeedback.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.LikeException;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private LikeServiceImpl likeService;
    private User user;
    private Review review;
    private Like likeToDelete;
    private AddLikeRequest request;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        review = new Review();
        review.setId(1L);

        likeToDelete = new Like();
        likeToDelete.setId(1L);
        likeToDelete.setUserId(user.getId());
        likeToDelete.setReviewId(review.getId());

        request = new AddLikeRequest();
        request.setEmail(user.getEmail());
        request.setReviewId(review.getId());
        request.setValue(true);
    }

    @Test
    @DisplayName("Успешно поставлен лайк на отзыв")
    public void testAddLike() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(likeRepository.findLikeByUserIdAndReviewId(user.getId(), review.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Like like = likeService.addLike(request);

        assertNotNull(like);
        assertEquals(user.getId(), like.getUserId());
        assertEquals(review.getId(), like.getReviewId());
        assertTrue(like.isValue());
        verify(userRepository, times(2)).findByEmail(request.getEmail());
        verify(reviewRepository).findById(request.getReviewId());
        verify(likeRepository).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("Успешно поставлен дизлайк на отзыв")
    public void testAddDislike() {
        request.setValue(false);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(likeRepository.findLikeByUserIdAndReviewId(user.getId(), review.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Like like = likeService.addLike(request);

        assertNotNull(like);
        assertEquals(user.getId(), like.getUserId());
        assertEquals(review.getId(), like.getReviewId());
        assertFalse(like.isValue());
        verify(userRepository, times(2)).findByEmail(request.getEmail());
        verify(reviewRepository).findById(request.getReviewId());
        verify(likeRepository).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("Лайк не поставлен, так как пользователь не найден")
    public void testAddLikeUserNotFound() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> likeService.addLike(request));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(reviewRepository, never()).findById(request.getReviewId());
        verify(likeRepository, never()).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Лайк не поставлен, так как отзыв не найден")
    public void testAddLikeReviewNotFound() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class, () -> likeService.addLike(request));
        assertEquals("Отзыв не найден", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(reviewRepository).findById(request.getReviewId());
        verify(likeRepository, never()).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Лайк не поставлен, так как пользователь уже поставил лайк/дизлайк на этот отзыв")
    public void testAddLikeAlreadyExists() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(likeRepository.findLikeByUserIdAndReviewId(user.getId(), review.getId())).thenReturn(Optional.of(new Like()));

        LikeException exception = assertThrows(LikeException.class, () -> likeService.addLike(request));
        assertEquals("Пользователь уже поставил лайк или дизлайк на этот отзыв", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(reviewRepository).findById(request.getReviewId());
        verify(likeRepository).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Лайк не поставлен, так как пользователь не может поставить лайк/дизлайк на свой отзыв")
    public void testAddLikeOwnReview() {
        review.setUserId(user.getId());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(request.getReviewId())).thenReturn(Optional.of(review));
        when(likeRepository.findLikeByUserIdAndReviewId(user.getId(), review.getId())).thenReturn(Optional.empty());

        LikeException exception = assertThrows(LikeException.class, () -> likeService.addLike(request));
        assertEquals("Пользователь не может поставить лайк/дизлайк на свой отзыв", exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(reviewRepository).findById(request.getReviewId());
        verify(likeRepository).findLikeByUserIdAndReviewId(user.getId(), review.getId());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Успешное удаление лайка/дизлайка")
    public void testDeleteLike() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(likeRepository.findById(likeToDelete.getId())).thenReturn(Optional.of(likeToDelete));

        boolean result = likeService.deleteLike(likeToDelete.getId(), user.getEmail());

        assertTrue(result);
        verify(userRepository).findByEmail(user.getEmail());
        verify(likeRepository, times(2)).findById(likeToDelete.getId());
        verify(likeRepository).delete(likeToDelete);
    }

    @Test
    @DisplayName("Лайк не удален, так как пользователь не найден")
    public void testDeleteLikeUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> likeService.deleteLike(likeToDelete.getId(), user.getEmail()));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(likeRepository, never()).findById(likeToDelete.getId());
        verify(likeRepository, never()).delete(likeToDelete);
    }

    @Test
    @DisplayName("Лайк не удален, так как не был найден")
    public void testDeleteLikeLikeNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(likeRepository.findById(likeToDelete.getId())).thenReturn(Optional.empty());

        LikeException exception =
                assertThrows(LikeException.class, () -> likeService.deleteLike(likeToDelete.getId(), user.getEmail()));
        assertEquals("Лайк/дизлайк не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(likeRepository).findById(likeToDelete.getId());
        verify(likeRepository, never()).delete(likeToDelete);
    }

    @Test
    @DisplayName("Лайк не удален, так как данный пользователь не ставил его")
    public void testDeleteLikeNotOwnedByUser() {
        likeToDelete.setUserId(2L);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(likeRepository.findById(likeToDelete.getId())).thenReturn(Optional.of(likeToDelete));

        LikeException exception =
                assertThrows(LikeException.class, () -> likeService.deleteLike(likeToDelete.getId(), user.getEmail()));
        assertEquals("Текущий пользователь не ставил лайк на этот отзыв", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(likeRepository).findById(likeToDelete.getId());
        verify(likeRepository, never()).delete(likeToDelete);
    }

    @Test
    @DisplayName("Успешное получение всех лайков на отзыв")
    public void testGetLikes() {
        when(likeRepository.getLikes(review.getId())).thenReturn(Set.of(new Like()));

        Set<Like> likes = likeService.getLikes(review.getId());

        assertNotNull(likes);
        assertEquals(1, likes.size());
        verify(likeRepository).getLikes(review.getId());
    }
}
