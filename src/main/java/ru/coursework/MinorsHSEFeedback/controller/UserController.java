package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetUserByEmailHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetUserReviewsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.UpdateUserHandler;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserRequest;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final GetUserByEmailHandler getUserByEmailHandler;
    private final GetUserReviewsHandler getUserReviewsHandler;
    private final UpdateUserHandler updateUserHandler;

    @Operation(summary = "Получить информацию о текущем пользователе")
    @GetMapping("/user")
    public UiUser getUserByEmail(@RequestParam String email) {
        return getUserByEmailHandler.handle(email);
    }

    @Operation(summary = "Получить все отзывы данного пользователя")
    @GetMapping("user/reviews")
    public Set<UiReview> getUserReviews(@RequestParam String email) {
        return getUserReviewsHandler.handle(email);
    }

    @Operation(summary = "Изменить информацию о пользователе")
    @PatchMapping("user")
    public UiUser updateUser(@RequestBody UpdateUserRequest request) {
        return updateUserHandler.handle(request);
    }
}
