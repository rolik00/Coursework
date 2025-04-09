package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UiUserMapperTest {
    @Mock
    private MinorRepository minorRepository;
    @InjectMocks
    private UiUserMapperImpl userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешный маппинг UiUser")
    public void applyTest() {
        User user = Instancio.create(User.class);
        Minor minor = Instancio.create(Minor.class);
        minor.setId(user.getMinorId());

        when(minorRepository.findById(user.getMinorId())).thenReturn(Optional.of(minor));

        UiUser result = userMapper.apply(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getCourseTitle(), result.getCourseTitle());
        assertEquals(minor.getTitle(), result.getMinorTitle());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRating(), result.getRating());
    }

    @Test
    @DisplayName("Маппер возращает null, так как User = null")
    public void applyNullTest() {
        UiUser result = userMapper.apply(null);
        assertNull(result);
    }
}
