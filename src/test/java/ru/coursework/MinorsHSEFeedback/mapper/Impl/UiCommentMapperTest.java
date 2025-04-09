package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UiCommentMapperTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UiCommentMapperImpl commentMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешный маппинг UiComment")
    public void applyTest() {
        Comment comment = Instancio.create(Comment.class);
        User user = Instancio.create(User.class);
        user.setId(comment.getUserId());

        when(userRepository.findById(comment.getUserId())).thenReturn(Optional.of(user));

        UiComment result = commentMapper.apply(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(comment.getReviewId(), result.getReviewId());
        assertEquals(user.getName(), result.getUserName());
        assertEquals(comment.getCreateDate(), result.getCreateDate());
        assertEquals(comment.getParentId(), result.getParentId());
    }

    @Test
    @DisplayName("Маппер возращает null, так как Comment = null")
    public void applyNullTest() {
        UiComment result = commentMapper.apply(null);
        assertNull(result);
    }
}
