package ru.coursework.MinorsHSEFeedback.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewPatch;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private MinorRepository minorRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private Minor minor;
    private Result result;
    private Review reviewToUpdateOrDelete;
    private CreateReviewRequest createReviewRequest;
    private UpdateReviewRequest updateReviewRequest;

    @BeforeEach
    public void setUp() {
        minor = new Minor();
        minor.setId(1L);
        minor.setTitle("Test Minor");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setMinorId(minor.getId());
        user.setCount(0);

        result = new Result();
        result.setMinorId(minor.getId());
        result.setReviewsCount(0);
        result.setDifficultyMarkSum(0);
        result.setInterestMarkSum(0);
        result.setTimeConsumptionMarkSum(0);
        result.setTotalMarkSum(0);

        reviewToUpdateOrDelete = new Review();
        reviewToUpdateOrDelete.setId(1L);
        reviewToUpdateOrDelete.setUserId(user.getId());
        reviewToUpdateOrDelete.setMinorId(minor.getId());
        reviewToUpdateOrDelete.setDifficultyMark(1);
        reviewToUpdateOrDelete.setInterestMark(2);
        reviewToUpdateOrDelete.setTimeConsumptionMark(3);
        reviewToUpdateOrDelete.setTotalMark(4);

        createReviewRequest = new CreateReviewRequest();
        createReviewRequest.setEmail(user.getEmail());
        createReviewRequest.setMinorTitle(minor.getTitle());
        createReviewRequest.setBody("Test Review");
        createReviewRequest.setDifficultyMark(1);
        createReviewRequest.setInterestMark(2);
        createReviewRequest.setTimeConsumptionMark(3);
        createReviewRequest.setTotalMark(4);

        updateReviewRequest = new UpdateReviewRequest();
        updateReviewRequest.setReviewId(1L);
        updateReviewRequest.setEmail(user.getEmail());
        UpdateReviewPatch patch = new UpdateReviewPatch();
        patch.setBody("Updated Review");
        patch.setInterestMark(3);
        updateReviewRequest.setPatch(patch);
    }

    @Test
    @DisplayName("Успешное создание отзыва")
    public void testCreateReview() {
        when(userRepository.findByEmail(createReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(createReviewRequest.getMinorTitle())).thenReturn(minor.getId());
        when(reviewRepository.findReviewByUserIdAndMinorId(user.getId(), minor.getId())).thenReturn(Optional.empty());
        when(resultRepository.findByMinorId(minor.getId())).thenReturn(Optional.of(result));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.createReview(createReviewRequest);

        assertNotNull(review);
        assertEquals(user.getId(), review.getUserId());
        assertEquals(minor.getId(), review.getMinorId());
        assertEquals(createReviewRequest.getBody(), review.getBody());
        assertEquals(createReviewRequest.getDifficultyMark(), review.getDifficultyMark());
        assertEquals(createReviewRequest.getInterestMark(), review.getInterestMark());
        assertEquals(createReviewRequest.getTimeConsumptionMark(), review.getTimeConsumptionMark());
        assertEquals(createReviewRequest.getTotalMark(), review.getTotalMark());
        assertEquals(LocalDate.now(), review.getCreateDate());

        assertEquals(1, user.getCount());
        assertEquals(1, result.getReviewsCount());
        assertEquals(createReviewRequest.getDifficultyMark(), result.getDifficultyMarkSum());
        assertEquals(createReviewRequest.getInterestMark(), result.getInterestMarkSum());
        assertEquals(createReviewRequest.getTimeConsumptionMark(), result.getTimeConsumptionMarkSum());
        assertEquals(createReviewRequest.getTotalMark(), result.getTotalMarkSum());

        verify(userRepository, times(2)).findByEmail(createReviewRequest.getEmail());
        verify(minorRepository).findByTitle(createReviewRequest.getMinorTitle());
        verify(reviewRepository).findReviewByUserIdAndMinorId(user.getId(), minor.getId());
        verify(reviewRepository).save(any(Review.class));
        verify(userRepository).save(user);
        verify(resultRepository).findByMinorId(minor.getId());
        verify(resultRepository).save(result);
    }

    @Test
    @DisplayName("Отзыв не создан, так как пользователь не найден")
    public void testCreateReviewUserNotFound() {
        when(userRepository.findByEmail(createReviewRequest.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> reviewService.createReview(createReviewRequest));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(createReviewRequest.getEmail());
        verify(minorRepository, never()).findByTitle(createReviewRequest.getMinorTitle());
        verify(reviewRepository, never()).findReviewByUserIdAndMinorId(user.getId(), minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(resultRepository, never()).save(result);
    }

    @Test
    @DisplayName("Отзыв не создан, так как пользователь не посещает этот майнор")
    public void testCreateReviewUserNotInMinor() {
        user.setMinorId(2L);

        when(userRepository.findByEmail(createReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(createReviewRequest.getMinorTitle())).thenReturn(minor.getId());

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(createReviewRequest));
        assertEquals("Пользователь не может оставить отзыв на этот майнор, так как не является его участником", exception.getMessage());
        verify(userRepository, times(2)).findByEmail(createReviewRequest.getEmail());
        verify(minorRepository).findByTitle(createReviewRequest.getMinorTitle());
        verify(reviewRepository).findReviewByUserIdAndMinorId(user.getId(), minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(resultRepository, never()).save(result);
    }

    @Test
    @DisplayName("Отзыв не создан, так как пользователь уже напил отзыв этот майнор")
    public void testCreateReviewAlreadyExists() {
        when(userRepository.findByEmail(createReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(createReviewRequest.getMinorTitle())).thenReturn(minor.getId());
        when(reviewRepository.findReviewByUserIdAndMinorId(user.getId(), minor.getId())).thenReturn(Optional.of(new Review()));

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(createReviewRequest));
        assertEquals("Пользователь уже писал отзыв для этого майнора", exception.getMessage());
        verify(userRepository, times(2)).findByEmail(createReviewRequest.getEmail());
        verify(minorRepository).findByTitle(createReviewRequest.getMinorTitle());
        verify(reviewRepository).findReviewByUserIdAndMinorId(user.getId(), minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(resultRepository, never()).save(result);
    }

    @Test
    @DisplayName("Отзыв не создан, превышен лимит количества отзывов для пользователя")
    public void testCreateReviewLimitExceeded() {
        user.setCount(4);

        when(userRepository.findByEmail(createReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(createReviewRequest.getMinorTitle())).thenReturn(minor.getId());

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(createReviewRequest));
        assertEquals("Превышен лимит количества отзывов для пользователя", exception.getMessage());
        verify(userRepository, times(2)).findByEmail(createReviewRequest.getEmail());
        verify(minorRepository).findByTitle(createReviewRequest.getMinorTitle());
        verify(reviewRepository).findReviewByUserIdAndMinorId(user.getId(), minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(resultRepository, never()).save(result);
    }

    @Test
    @DisplayName("Успешное обновление отзыва")
    public void testUpdateReview() {
        when(userRepository.findByEmail(updateReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(updateReviewRequest.getReviewId())).thenReturn(Optional.of(reviewToUpdateOrDelete));
        when(resultRepository.findByMinorId(minor.getId())).thenReturn(Optional.of(result));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.updateReview(updateReviewRequest);

        assertNotNull(review);
        assertEquals(updateReviewRequest.getPatch().getBody(), review.getBody());
        assertEquals(reviewToUpdateOrDelete.getDifficultyMark(), review.getDifficultyMark());
        assertEquals(updateReviewRequest.getPatch().getInterestMark(), review.getInterestMark());
        assertEquals(reviewToUpdateOrDelete.getTimeConsumptionMark(), review.getTimeConsumptionMark());
        assertEquals(reviewToUpdateOrDelete.getTotalMark(), review.getTotalMark());

        verify(userRepository).findByEmail(updateReviewRequest.getEmail());
        verify(reviewRepository, times(2)).findById(updateReviewRequest.getReviewId());
        verify(resultRepository).findByMinorId(minor.getId());
        verify(reviewRepository).save(review);
        verify(resultRepository).save(result);
    }

    @Test
    @DisplayName("Отзыв не обновлен, так как пользователь не найден")
    public void testUpdateUserNotFound() {
        when(userRepository.findByEmail(updateReviewRequest.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> reviewService.updateReview(updateReviewRequest));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(updateReviewRequest.getEmail());
        verify(reviewRepository, never()).findById(updateReviewRequest.getReviewId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Отзыв не обновлен, так как отзыв не найден")
    public void testUpdateReviewNotFound() {
        when(userRepository.findByEmail(updateReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewToUpdateOrDelete.getId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.updateReview(updateReviewRequest));
        assertEquals("Отзыв не найден", exception.getMessage());
        verify(userRepository).findByEmail(updateReviewRequest.getEmail());
        verify(reviewRepository).findById(updateReviewRequest.getReviewId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Отзыв не обновлен, так как данный пользователь не является создателем")
    public void testUpdateUserNotCreator() {
        reviewToUpdateOrDelete.setUserId(2L);

        when(userRepository.findByEmail(updateReviewRequest.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(updateReviewRequest.getReviewId())).thenReturn(Optional.of(reviewToUpdateOrDelete));

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.updateReview(updateReviewRequest));
        assertEquals("Текущий пользователь не является создателем данного отзыва", exception.getMessage());
        verify(userRepository).findByEmail(updateReviewRequest.getEmail());
        verify(reviewRepository).findById(updateReviewRequest.getReviewId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    @DisplayName("Успешное удаление отзыва")
    public void testDeleteReview() {
        user.setCount(1);
        result.setReviewsCount(1);
        result.setDifficultyMarkSum(reviewToUpdateOrDelete.getDifficultyMark());
        result.setInterestMarkSum(reviewToUpdateOrDelete.getInterestMark());
        result.setTimeConsumptionMarkSum(reviewToUpdateOrDelete.getTimeConsumptionMark());
        result.setTotalMarkSum(reviewToUpdateOrDelete.getTotalMark());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewToUpdateOrDelete.getId())).thenReturn(Optional.of(reviewToUpdateOrDelete));
        when(resultRepository.findByMinorId(minor.getId())).thenReturn(Optional.of(result));

        boolean resultOfDelete = reviewService.deleteReview(reviewToUpdateOrDelete.getId(), user.getEmail());

        assertTrue(resultOfDelete);
        assertEquals(0, user.getCount());
        assertEquals(0, result.getReviewsCount());
        assertEquals(0, result.getDifficultyMarkSum());
        assertEquals(0, result.getInterestMarkSum());
        assertEquals(0, result.getTimeConsumptionMarkSum());
        assertEquals(0, result.getTotalMarkSum());

        verify(userRepository, times(2)).findByEmail(user.getEmail());
        verify(reviewRepository, times(2)).findById(reviewToUpdateOrDelete.getId());
        verify(resultRepository).findByMinorId(minor.getId());
        verify(userRepository).save(user);
        verify(resultRepository).save(result);
        verify(reviewRepository).delete(reviewToUpdateOrDelete);
        verify(likeRepository).deleteByReviewId(reviewToUpdateOrDelete.getId());
        verify(commentRepository).deleteByReviewId(reviewToUpdateOrDelete.getId());
    }

    @Test
    @DisplayName("Отзыв не удален, так как пользователь не найден")
    public void testDeleteUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> reviewService.deleteReview(reviewToUpdateOrDelete.getId(), user.getEmail()));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository, never()).findById(reviewToUpdateOrDelete.getId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).save(result);
        verify(reviewRepository, never()).delete(reviewToUpdateOrDelete);
        verify(likeRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
        verify(commentRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
    }

    @Test
    @DisplayName("Отзыв не удален, так как отзыв не найден")
    public void testDeleteReviewNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewToUpdateOrDelete.getId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.updateReview(updateReviewRequest));
        assertEquals("Отзыв не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findById(reviewToUpdateOrDelete.getId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).save(result);
        verify(reviewRepository, never()).delete(reviewToUpdateOrDelete);
        verify(likeRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
        verify(commentRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
    }

    @Test
    @DisplayName("Отзыв не удален, так как данный пользователь не является создателем")
    public void testDeleteUserNotCreator() {
        reviewToUpdateOrDelete.setUserId(2L);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewToUpdateOrDelete.getId())).thenReturn(Optional.of(reviewToUpdateOrDelete));

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.updateReview(updateReviewRequest));
        assertEquals("Текущий пользователь не является создателем данного отзыва", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findById(reviewToUpdateOrDelete.getId());
        verify(resultRepository, never()).findByMinorId(minor.getId());
        verify(userRepository, never()).save(user);
        verify(resultRepository, never()).save(result);
        verify(reviewRepository, never()).delete(reviewToUpdateOrDelete);
        verify(likeRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
        verify(commentRepository, never()).deleteByReviewId(reviewToUpdateOrDelete.getId());
    }

    @Test
    @DisplayName("Успешное получение всех отзывов")
    public void testGetReviews() {
        Review review1 = new Review();
        review1.setId(1L);
        review1.setMinorId(minor.getId());
        Review review2 = new Review();
        review2.setId(2L);
        review2.setMinorId(minor.getId());
        Set<Review> reviews = new HashSet<>(Arrays.asList(review1, review2));

        when(reviewRepository.getReviews(minor.getId())).thenReturn(reviews);
        when(commentRepository.countCommentsByReviewIds(anySet())).thenReturn(0);
        when(likeRepository.getCountLikesByReviewIds(anySet())).thenReturn(0);
        when(likeRepository.getCountDislikesByReviewIds(anySet())).thenReturn(0);

        Set<Review> result = reviewService.getReviews(minor.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reviewRepository).getReviews(minor.getId());
        verify(commentRepository, times(4)).countCommentsByReviewIds(anySet());
        verify(likeRepository, times(4)).getCountLikesByReviewIds(anySet());
        verify(likeRepository, times(4)).getCountDislikesByReviewIds(anySet());
    }

    @Test
    @DisplayName("Успешное получение отзывов данного пользователя")
    public void testGetReviewsByUser() {
        Review review1 = new Review();
        review1.setId(1L);
        review1.setMinorId(minor.getId());
        Review review2 = new Review();
        review2.setId(2L);
        review2.setMinorId(minor.getId());
        Set<Review> reviews = new HashSet<>(Arrays.asList(review1, review2));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findReviewByUserId(user.getId())).thenReturn(reviews);
        when(reviewRepository.getReviews(minor.getId())).thenReturn(reviews);
        when(commentRepository.countCommentsByReviewIds(anySet())).thenReturn(0);
        when(likeRepository.getCountLikesByReviewIds(anySet())).thenReturn(0);
        when(likeRepository.getCountDislikesByReviewIds(anySet())).thenReturn(0);

        Set<Review> result = reviewService.getReviewsByUser(user.getEmail());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findReviewByUserId(user.getId());
        verify(reviewRepository, times(2)).getReviews(minor.getId());
        verify(commentRepository, times(4)).countCommentsByReviewIds(anySet());
        verify(likeRepository, times(4)).getCountLikesByReviewIds(anySet());
        verify(likeRepository, times(4)).getCountDislikesByReviewIds(anySet());
    }

    @Test
    @DisplayName("Отзывы не получены, так пользователь не найден")
    public void testGetReviewsByUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> reviewService.getReviewsByUser(user.getEmail()));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository, never()).findReviewByUserId(user.getId());
        verify(reviewRepository, never()).getReviews(minor.getId());
        verify(commentRepository, never()).countCommentsByReviewIds(anySet());
        verify(likeRepository, never()).getCountLikesByReviewIds(anySet());
        verify(likeRepository, never()).getCountDislikesByReviewIds(anySet());
    }
}
