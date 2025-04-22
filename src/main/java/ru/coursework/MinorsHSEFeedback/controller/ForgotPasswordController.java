package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.extraFunctions.RandomString;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final UserService userService;
    private final EmailSender emailSender;
    private final UiUserMapper uiUserMapper;

    private RandomString randomString = new RandomString();
    private static final String BASE_URL = "http://localhost:3000";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$");

    @Operation(summary = "Отправка временного токена на почту")
    @PostMapping("/forgot_password")
    public ResponseEntity<String> processForgotPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("invoke processForgotPassword");
        String token = randomString.randomAlphanumericString(30);

        try {
            userService.updateResetPasswordToken(token, request.getEmail());
            String resetPasswordLink = BASE_URL + "/reset_password?token=" + token;  //скорректировать ссылку для перебрасывания пользователя
            String userName = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                    .getName();
            String content = BEGIN.getTitle() + userName + RESET_PASSWORD_1.getTitle() + resetPasswordLink + RESET_PASSWORD_2.getTitle() + END.getTitle();
            emailSender.sendEmail(request.getEmail(), "Восстановление пароля на портале MinorsHSEFeedbacks", content);
            log.info("Токен {} для восстановление пароля отправлен на почту пользователю {}", token, request.getEmail());
        } catch (UsernameNotFoundException ex) {
            log.error("Пользователь {} не найден", request.getEmail());
            ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при отправке токена на почту пользователя {}", request.getEmail());
            ResponseEntity.badRequest().body("Ошибка при отправке токена на почту");
        }

        return ResponseEntity.ok("Токен успешно отправлен на почту");
    }

    @Operation(summary = "Восстановление пароля")
    @PostMapping("/reset_password")
    public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("invoke processResetPassword");
        User user = userService.getByResetPasswordToken(request.getToken());

        if (user == null) {
            log.error("Пользователь по токену {} не найден", request.getToken());
            return ResponseEntity.badRequest().body("Invalid Token");
        } else {
            if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
                log.error("User = {}, error = {} ", user.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
                return ResponseEntity.badRequest().body(UNRELIABLE_PASSWORD_ERROR.getTitle());
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                log.error("User = {}, error = {}", user.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
                return ResponseEntity.badRequest().body(PASSWORD_NOT_MATCH_ERROR.getTitle());
            }

            userService.updatePassword(user, request.getPassword());

            log.info("Пароль пользователя {} восстановлен", user.getEmail());
        }

        return ResponseEntity.ok(uiUserMapper.apply(user));
    }
}