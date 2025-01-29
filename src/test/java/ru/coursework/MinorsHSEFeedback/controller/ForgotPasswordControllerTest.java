package ru.coursework.MinorsHSEFeedback.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.PASSWORD_NOT_MATCH_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.UNRELIABLE_PASSWORD_ERROR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.request.ForgotPasswordRequest;
import ru.coursework.MinorsHSEFeedback.request.ResetPasswordRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.Optional;

@SpringBootTest
public class ForgotPasswordControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ExtendedModelMap();
    }

    @Test
    public void testProcessForgotPassword_UserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistent@example.com");

        when(userService.findByEmail(request.getEmail())).thenThrow(new UsernameNotFoundException("User not found"));

        String viewName = forgotPasswordController.processForgotPassword(request, model);

        assertEquals("forgot_password_form", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals("User not found", model.getAttribute("error"));
        verify(userService, times(1)).findByEmail(request.getEmail());
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testProcessForgotPassword_Success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@edu.hse.ru");

        User user = new User();
        user.setName("Test User");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(userService).updateResetPasswordToken(anyString(), anyString());
        doNothing().when(emailSender).sendEmail(anyString(), anyString(), anyString());

        String viewName = forgotPasswordController.processForgotPassword(request, model);

        assertEquals("forgot_password_form", viewName);
        assertTrue(model.containsAttribute("message"));
        assertEquals("We have sent a reset password link to your email. Please check.", model.getAttribute("message"));
        verify(userService, times(1)).findByEmail(request.getEmail());
        verify(userService, times(1)).updateResetPasswordToken(anyString(), eq(request.getEmail()));
        verify(emailSender, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testProcessForgotPassword_EmailSendingError() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@edu.hse.ru");

        User user = new User();
        user.setName("Test User");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(userService).updateResetPasswordToken(anyString(), anyString());
        doThrow(new RuntimeException("Email sending error")).when(emailSender).sendEmail(anyString(), anyString(), anyString());

        String viewName = forgotPasswordController.processForgotPassword(request, model);

        assertEquals("forgot_password_form", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals("Error while sending email", model.getAttribute("error"));
        verify(userService, times(1)).findByEmail(request.getEmail());
        verify(userService, times(1)).updateResetPasswordToken(anyString(), eq(request.getEmail()));
        verify(emailSender, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testProcessResetPassword_InvalidToken() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("invalidToken");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");

        when(userService.getByResetPasswordToken(request.getToken())).thenReturn(null);

        String viewName = forgotPasswordController.processResetPassword(request, model);

        assertEquals("message", viewName);
        assertTrue(model.containsAttribute("message"));
        assertEquals("Invalid Token", model.getAttribute("message"));
        verify(userService, times(1)).getByResetPasswordToken(request.getToken());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testProcessResetPassword_UnreliablePassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("validToken");
        request.setPassword("weakpassword");
        request.setConfirmPassword("weakpassword");

        User user = new User();
        when(userService.getByResetPasswordToken(request.getToken())).thenReturn(user);

        String viewName = forgotPasswordController.processResetPassword(request, model);

        assertEquals("error", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals(UNRELIABLE_PASSWORD_ERROR.getTitle(), model.getAttribute("error"));
        verify(userService, times(1)).getByResetPasswordToken(request.getToken());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testProcessResetPassword_PasswordsDoNotMatch() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("validToken");
        request.setPassword("Password1@");
        request.setConfirmPassword("DifferentPassword1@");

        User user = new User();
        when(userService.getByResetPasswordToken(request.getToken())).thenReturn(user);

        String viewName = forgotPasswordController.processResetPassword(request, model);

        assertEquals("error", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals(PASSWORD_NOT_MATCH_ERROR.getTitle(), model.getAttribute("error"));
        verify(userService, times(1)).getByResetPasswordToken(request.getToken());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testProcessResetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("validToken");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");

        User user = new User();
        when(userService.getByResetPasswordToken(request.getToken())).thenReturn(user);
        doNothing().when(userService).updatePassword(any(User.class), anyString());

        String viewName = forgotPasswordController.processResetPassword(request, model);

        assertEquals("message", viewName);
        assertTrue(model.containsAttribute("message"));
        assertEquals("You have successfully changed your password.", model.getAttribute("message"));
        verify(userService, times(1)).getByResetPasswordToken(request.getToken());
        verify(userService, times(1)).updatePassword(any(User.class), eq(request.getPassword()));
        verifyNoMoreInteractions(userService);
    }
}
