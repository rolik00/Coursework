package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.extraFunctions.RandomString;
import ru.coursework.MinorsHSEFeedback.request.ForgotPasswordRequest;
import ru.coursework.MinorsHSEFeedback.request.ResetPasswordRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.regex.Pattern;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.PASSWORD_NOT_MATCH_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.UNRELIABLE_PASSWORD_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.BEGIN;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.END;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.RESET_PASSWORD_1;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.RESET_PASSWORD_2;

@Controller
@Slf4j
public class ForgotPasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailSender emailSender;

    private RandomString randomString = new RandomString();
    private static String BASE_URL = "http://localhost:8080";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$");

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";
    }

    @Operation(summary = "Отправка временного токена на почту")
    @PostMapping("/forgot_password")
    public String processForgotPassword(/*@RequestBody*/ @ModelAttribute ForgotPasswordRequest request, Model model) {
        log.info("invoke processForgotPassword");
        String token = randomString.randomAlphanumericString(30);

        try {
            userService.updateResetPasswordToken(token, request.getEmail());
            String resetPasswordLink = BASE_URL + "/reset_password?token=" + token;
            String userName = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                    .getName();
            String content = BEGIN.getTitle() + userName + RESET_PASSWORD_1.getTitle() + resetPasswordLink + RESET_PASSWORD_2.getTitle() + END.getTitle();
            emailSender.sendEmail(request.getEmail(), "Восстановление пароля на портале MinorsHSEFeedbacks", content);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
            log.info("Токен {} для восстановление пароля отправлен на почту пользователю {}", token, request.getEmail());
        } catch (UsernameNotFoundException ex) {
            log.error("Пользователь {} не найден", request.getEmail());
            model.addAttribute("error", ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при отправке токена на почту пользователя {}", request.getEmail());
            model.addAttribute("error", "Error while sending email");
        }

        return "forgot_password_form";
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        }

        return "reset_password_form";
    }

    @Operation(summary = "Восстановление пароля")
    @PostMapping("/reset_password")
    public String processResetPassword(/*@RequestBody*/ @ModelAttribute ResetPasswordRequest request, Model model) {
        log.info("invoke processResetPassword");
        User user = userService.getByResetPasswordToken(request.getToken());
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            log.error("Пользователь по токену {} не найден", request.getToken());
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {
            if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
                log.error("User = {}, error = {} ", user.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
                model.addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
                return "error";
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                log.error("User = {}, error = {}", user.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
                model.addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
                return "error";
            }

            userService.updatePassword(user, request.getPassword());

            log.info("Пароль пользователя {} восстановлен", user.getEmail());

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "message";
    }
}