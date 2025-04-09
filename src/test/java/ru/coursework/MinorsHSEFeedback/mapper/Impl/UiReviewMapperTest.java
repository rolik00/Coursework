package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UiReviewMapperTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MinorRepository minorRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeRepository likeRepository;
    @InjectMocks
    private UiReviewMapperImpl reviewMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешный маппинг UiReview")
    public void applyTest() {
        Random random = new Random();
        Review review = Instancio.create(Review.class);
        User user = Instancio.create(User.class);
        user.setId(review.getUserId());
        Minor minor = Instancio.create(Minor.class);
        minor.setId(review.getMinorId());
        int commentCount = random.nextInt(0, 10);
        int likeCount = random.nextInt(0, 10);
        int dislikeCount = random.nextInt(0, 10);

        when(userRepository.findById(review.getUserId())).thenReturn(Optional.of(user));
        when(minorRepository.findById(review.getMinorId())).thenReturn(Optional.of(minor));
        when(commentRepository.countCommentsByReviewIds(Set.of(review.getId()))).thenReturn(commentCount);
        when(likeRepository.getCountLikesByReviewIds(Set.of(review.getId()))).thenReturn(likeCount);
        when(likeRepository.getCountDislikesByReviewIds(Set.of(review.getId()))).thenReturn(dislikeCount);

        UiReview result = reviewMapper.apply(review);

        assertNotNull(result);
        assertEquals(review.getId(), result.getId());
        assertEquals(user.getName(), result.getUserName());
        assertEquals(minor.getTitle(), result.getMinorTitle());
        assertEquals(review.getBody(), result.getBody());
        assertEquals(review.getDifficultyMark(), result.getDifficultyMark());
        assertEquals(review.getInterestMark(), result.getInterestMark());
        assertEquals(review.getTimeConsumptionMark(), result.getTimeConsumptionMark());
        assertEquals(review.getTotalMark(), result.getTotalMark());
        assertEquals(review.getCreateDate(), result.getCreateDate());
        assertEquals(commentCount, result.getCommentsCount());
        assertEquals(likeCount, result.getLikesCount());
        assertEquals(dislikeCount, result.getDislikesCount());
        assertEquals(review.getValue(), result.getValue());
    }

    @Test
    @DisplayName("Маппер возращает null, так как Review = null")
    public void applyNullTest() {
        UiReview result = reviewMapper.apply(null);
        assertNull(result);
    }
}
