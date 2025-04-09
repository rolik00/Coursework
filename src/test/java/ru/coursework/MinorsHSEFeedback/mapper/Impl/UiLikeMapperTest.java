package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UiLikeMapperTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UiLikeMapperImpl likeMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешный маппинг UiLike")
    public void applyTest() {
        Like like = Instancio.create(Like.class);
        User user = Instancio.create(User.class);
        user.setId(like.getUserId());

        when(userRepository.findById(like.getUserId())).thenReturn(Optional.of(user));

        UiLike result = likeMapper.apply(like);

        assertNotNull(result);
        assertEquals(like.getId(), result.getId());
        assertEquals(like.isValue(), result.isValue());
        assertEquals(like.getReviewId(), result.getReviewId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Маппер возращает null, так как Like = null")
    public void applyNullTest() {
        UiLike result = likeMapper.apply(null);
        assertNull(result);
    }
}
