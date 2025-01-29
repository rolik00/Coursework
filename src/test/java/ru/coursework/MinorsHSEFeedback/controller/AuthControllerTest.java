package ru.coursework.MinorsHSEFeedback.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.request.RegistrationRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdatePasswordRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.security.Principal;
import java.util.Optional;

@SpringBootTest
public class AuthControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private EmailSender emailSender;
    @InjectMocks
    private AuthController authController;

    private Model model;
    private Principal principal;

    @BeforeEach
    public void setUp() {
        model = mock(Model.class);
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@edu.hse.ru");
    }

    @Test
    public void testProcessRegister_UserExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("existing@edu.hse.ru");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        String viewName = authController.processRegister(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("error", IS_EXIST_ERROR.getTitle());
        verify(userService, times(1)).findByEmail(request.getEmail());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testProcessRegister_InvalidEmail() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("invalid@example.com");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        String viewName = authController.processRegister(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("error", IS_NOT_HSE_ERROR.getTitle());
        verify(userService, times(1)).findByEmail(request.getEmail());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testProcessRegister_UnreliablePassword() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("valid@edu.hse.ru");
        request.setPassword("weakpassword");
        request.setConfirmPassword("weakpassword");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        String viewName = authController.processRegister(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
        verify(userService, times(1)).findByEmail(request.getEmail());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testProcessRegister_PasswordsDoNotMatch() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("valid@edu.hse.ru");
        request.setPassword("Password1@");
        request.setConfirmPassword("DifferentPassword1@");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        String viewName = authController.processRegister(request, model);

        assertEquals("error", viewName);
        verify(model).addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
        verify(userService, times(1)).findByEmail(request.getEmail());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testProcessRegister_Success() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("valid@edu.hse.ru");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");
        request.setName("Test User");
        request.setMinorTitle("Minor Title");
        request.setCourseTitle("Course Title");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        String viewName = authController.processRegister(request, model);

        assertEquals("register_success", viewName);
        verify(userService, times(1)).findByEmail(request.getEmail());
        verify(userService, times(1)).save(any(User.class));
        verify(emailSender, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdatePassword_UserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("CurrentPassword1@");
        request.setNewPassword("NewPassword1@");
        request.setConfirmNewPassword("NewPassword1@");

        when(userService.findByEmail("user@edu.hse.ru")).thenThrow(new UsernameNotFoundException("User not found"));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("error", "User not found");
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testUpdatePassword_IncorrectCurrentPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("NewPassword1@");
        request.setConfirmNewPassword("NewPassword1@");

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode("CurrentPassword1@"));

        when(userService.findByEmail("user@edu.hse.ru")).thenReturn(Optional.of(user));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("error", UNCORRECT_PASSWORD_ERROR.getTitle());
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testUpdatePassword_NewPasswordMatchesCurrent() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("CurrentPassword1@");
        request.setNewPassword("CurrentPassword1@");
        request.setConfirmNewPassword("CurrentPassword1@");

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode("CurrentPassword1@"));

        when(userService.findByEmail("user@edu.hse.ru")).thenReturn(Optional.of(user));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("error", PASSWORD_MATCH_ERROR.getTitle());
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testUpdatePassword_PasswordsDoNotMatch() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("CurrentPassword1@");
        request.setNewPassword("NewPassword1@");
        request.setConfirmNewPassword("DifferentPassword1@");

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode("CurrentPassword1@"));

        when(userService.findByEmail("user@edu.hse.ru")).thenReturn(Optional.of(user));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testUpdatePassword_UnreliablePassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("CurrentPassword1@");
        request.setNewPassword("weakpassword");
        request.setConfirmNewPassword("weakpassword");

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode("CurrentPassword1@"));

        when(userService.findByEmail("user@edu.hse.ru")).thenReturn(Optional.of(user));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testUpdatePassword_Success() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("CurrentPassword1@");
        request.setNewPassword("NewPassword1@");
        request.setConfirmNewPassword("NewPassword1@");

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode("CurrentPassword1@"));
        user.setEmail("user@edu.hse.ru");
        user.setName("Test User");

        when(userService.findByEmail("user@edu.hse.ru")).thenReturn(Optional.of(user));

        String viewName = authController.updatePassword(request, principal, model);

        assertEquals("update_password", viewName);
        verify(model).addAttribute("message", "Password updated successfully");
        verify(userService, times(1)).findByEmail("user@edu.hse.ru");
        verify(userService, times(1)).save(any(User.class));
        verify(emailSender, times(1)).sendEmail(anyString(), anyString(), anyString());
    }
}
