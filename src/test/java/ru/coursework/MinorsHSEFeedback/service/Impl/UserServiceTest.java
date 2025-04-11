package ru.coursework.MinorsHSEFeedback.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserPatch;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserRequest;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.IS_NOT_HSE_ERROR;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private MinorRepository minorRepository;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@edu.hse.ru");
        user.setName("Test User");
        user.setMinorId(1L);
        user.setCourseTitle("Course Title");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("test@edu.hse.ru");
        UpdateUserPatch patch = new UpdateUserPatch();
        patch.setName("Updated User");
        patch.setMinorTitle("New Minor");
        patch.setCourseTitle("New Course Title");
        patch.setEmail("new_test@edu.hse.ru");
        updateUserRequest.setPatch(patch);
    }

    @Test
    @DisplayName("Успешно создан токен для сброса пароля")
    public void testUpdateResetPasswordToken() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateResetPasswordToken("token", "test@edu.hse.ru");

        assertEquals("token", user.getResetPasswordToken());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Токен не создан, так как пользователь не найден")
    public void testUpdateResetPasswordTokenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                        () -> userService.updateResetPasswordToken("token", "test@edu.hse.ru"));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Успешно получен токен для сброса пароля")
    public void testGetByResetPasswordToken() {
        when(userRepository.findByResetPasswordToken("token")).thenReturn(user);

        User result = userService.getByResetPasswordToken("token");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Успешно обновлен пароль")
    public void testUpdatePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userService.updatePassword(user, "newPassword");

        assertTrue(passwordEncoder.matches("newPassword", user.getPassword()));
        assertNull(user.getResetPasswordToken());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Успешное получение всех пользователей")
    public void testFindAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(reviewService.getReviewsByUser(user.getEmail())).thenReturn(new HashSet<>());

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.getFirst());
        assertEquals(0, users.getFirst().getRating());
        verify(userRepository).findAll();
        verify(reviewService).getReviewsByUser(user.getEmail());
    }

    @Test
    @DisplayName("Успешное получение пользователя по логину")
    public void testFindByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(user.getEmail());

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("Успешное обновление информации о пользователе")
    public void testUpdateUser() {
        Minor minor = new Minor();
        minor.setId(2L);
        minor.setTitle("New Minor");

        when(userRepository.findByEmail(updateUserRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(updateUserRequest.getPatch().getMinorTitle())).thenReturn(minor.getId());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUserRequest);

        assertNotNull(result);
        assertEquals(updateUserRequest.getPatch().getName(), result.getName());
        assertEquals(minor.getId(), result.getMinorId());
        assertEquals(updateUserRequest.getPatch().getCourseTitle(), result.getCourseTitle());
        assertEquals(updateUserRequest.getPatch().getEmail(), result.getEmail());
        verify(userRepository).findByEmail(updateUserRequest.getEmail());
        verify(minorRepository).findByTitle(updateUserRequest.getPatch().getMinorTitle());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Информация о пользователе не обновлена, так как он не найден")
    public void testUpdateUserNotFound() {
        when(userRepository.findByEmail(updateUserRequest.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> userService.updateUser(updateUserRequest));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByEmail(updateUserRequest.getEmail());
        verify(minorRepository, never()).findByTitle(updateUserRequest.getPatch().getMinorTitle());
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Информация о пользователе не обновлена, так как новая почта некорректна")
    public void testUpdateUserInvalidEmail() {
        Minor minor = new Minor();
        minor.setId(2L);
        minor.setTitle("New Minor");
        updateUserRequest.getPatch().setEmail("invalid@example.com");

        when(userRepository.findByEmail(updateUserRequest.getEmail())).thenReturn(Optional.of(user));
        when(minorRepository.findByTitle(updateUserRequest.getPatch().getMinorTitle())).thenReturn(minor.getId());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> userService.updateUser(updateUserRequest));
        assertEquals(IS_NOT_HSE_ERROR.getTitle(), exception.getMessage());
        verify(userRepository).findByEmail(updateUserRequest.getEmail());
        verify(minorRepository).findByTitle(updateUserRequest.getPatch().getMinorTitle());
        verify(userRepository, never()).save(user);
    }
}
