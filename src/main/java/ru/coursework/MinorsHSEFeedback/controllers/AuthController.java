package ru.coursework.MinorsHSEFeedback.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.db.Security;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.service.SecurityService;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$");



    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        String login = requestBody.get("login");
        String password = requestBody.get("password");

        if (!userService.findByLogin(login).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с такой почтой уже зарегистрирован!");
        }

        if (!login.endsWith("@edu.hse.ru")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не является корпоративной почтой ВШЭ");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }

        User user = new User();
        user.setName(name);
        user.setLogin(login);
        userService.save(user);

        String encodedPassword = passwordEncoder.encode(password);
        Security security = new Security();
        security.setLogin(login);
        security.setPassword(encodedPassword);
        securityService.save(security);

        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestBody) {
        String login = requestBody.get("login");
        String password = requestBody.get("password");

        Optional<Security> security = securityService.findByLogin(login);
        if (security.isPresent()) {
            if (passwordEncoder.matches(password, security.get().getPassword())) { // Use BCryptPasswordEncoder to match passwords
                return ResponseEntity.ok("Пользователь авторизован!");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Введен неправильный логин или пароль");
    }
}
