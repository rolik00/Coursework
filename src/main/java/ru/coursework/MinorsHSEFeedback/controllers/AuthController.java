package ru.coursework.MinorsHSEFeedback.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.db.Security;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.requests.LoginRequest;
import ru.coursework.MinorsHSEFeedback.requests.RegistrationRequest;
import ru.coursework.MinorsHSEFeedback.service.SecurityService;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$");

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody RegistrationRequest request) {

        if (userService.findByLogin(request.getLogin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с такой почтой уже зарегистрирован!");
        }

        if (!request.getLogin().endsWith("@edu.hse.ru")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не является корпоративной почтой ВШЭ");
        }

        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }

        User user = User.builder().
                name(request.getName())
                .courseTitle(request.getCourseTitle())
                .minorTitle(request.getMinorTitle())
                .login(request.getLogin())
                .build();
        userService.save(user);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Security security = new Security();
        security.setLogin(request.getLogin());
        security.setPassword(encodedPassword);
        securityService.save(security);

        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("Пользователь авторизован!");
    }
}
