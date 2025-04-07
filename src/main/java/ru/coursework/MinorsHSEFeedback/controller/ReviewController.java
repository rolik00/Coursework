package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.controller.handler.AddLikeHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.CreateCommentHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.CreateReviewHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetCommentsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetLikesHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetReviewsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetReviewsSortedByDateHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.UpdateCommentHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.UpdateReviewHandler;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.DeleteRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;
import ru.coursework.MinorsHSEFeedback.service.CommentService;
import ru.coursework.MinorsHSEFeedback.service.LikeService;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/minor")
@RequiredArgsConstructor
public class ReviewController {
    private final CreateReviewHandler createReviewHandler;
    private final UpdateReviewHandler updateReviewHandler;
    private final GetReviewsHandler getReviewsHandler;
    private final GetReviewsSortedByDateHandler getReviewsSortedByDateHandler;
    private final CreateCommentHandler createCommentHandler;
    private final UpdateCommentHandler updateCommentHandler;
    private final GetCommentsHandler getCommentsHandler;
    private final AddLikeHandler addLikeHandler;
    private final GetLikesHandler getLikesHandler;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Operation(summary = "Создать отзыв на майнор")
    @PostMapping("/review")
    public UiReview createReview(@RequestBody CreateReviewRequest request) {
        return createReviewHandler.handle(request);
    }

    @Operation(summary = "Обновить отзыв на майнор")
    @PatchMapping("/review")
    public UiReview updateReview(@RequestBody UpdateReviewRequest request) {
        return updateReviewHandler.handle(request);
    }

    @Operation(summary = "Удалить отзыв на майнор")
    @DeleteMapping("/review")
    public boolean deleteReview(@RequestBody DeleteRequest request) {
        return reviewService.deleteReview(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить отзывы на майнор")
    @GetMapping("/review")
    public List<UiReview> getReviews(@RequestParam Long minorId) {
        return getReviewsHandler.handle(minorId);
    }

    @Operation(summary = "Получить отзывы на майнор, сортированные по дате создания")
    @GetMapping("/review/sortByDate")
    public List<UiReview> getReviewsSortByDate(@RequestParam Long minorId) {
        return getReviewsSortedByDateHandler.handle(minorId);
    }

    @Operation(summary = "Создать комментарий на майнор")
    @PostMapping("/review/comment")
    public UiComment createComment(@RequestBody CreateCommentRequest request) {
        return createCommentHandler.handle(request);
    }

    @Operation(summary = "Обновить отзыв на майнор")
    @PatchMapping("/review/comment")
    public UiComment updateComment(@RequestBody UpdateCommentRequest request) {
        return updateCommentHandler.handle(request);
    }

    @Operation(summary = "Удалить отзыв на майнор")
    @DeleteMapping("/review/comment")
    public boolean deleteComment(@RequestBody DeleteRequest request) {
        return commentService.deleteComment(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить комментарии к отзыву")
    @GetMapping("/review/comment")
    public Set<UiComment> getComments(@RequestParam Long reviewId) {
        return getCommentsHandler.handle(reviewId);
    }

    @Operation(summary = "Поставить лайк/дизлайк на отзыв")
    @PostMapping("/review/like")
    public UiLike addLike(@RequestBody AddLikeRequest request) {
        return addLikeHandler.handle(request);
    }

    @Operation(summary = "Удалить лайк/дизлайк с отзыва")
    @DeleteMapping("/review/like")
    public boolean deleteLike(@RequestBody DeleteRequest request) {
        return likeService.deleteLike(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить все лайки и дизлайки к отзыву")
    @GetMapping("/review/like")
    public Set<UiLike> getLikes(@RequestParam Long reviewId) {
        return getLikesHandler.handle(reviewId);
    }
}
