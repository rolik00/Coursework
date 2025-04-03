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
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;
import ru.coursework.MinorsHSEFeedback.request.CreateCommentRequest;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.DeleteLikeRequest;
import ru.coursework.MinorsHSEFeedback.request.DeleteReviewOrCommentRequest;
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
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Operation(summary = "Создать отзыв на майнор")
    @PostMapping("/review")
    public UiReview createReview(@RequestBody CreateReviewRequest request) {
        return reviewService.createReview(request);
    }

    @Operation(summary = "Обновить отзыв на майнор")
    @PatchMapping("/review")
    public UiReview updateReview(@RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(request);
    }

    @Operation(summary = "Удалить отзыв на майнор")
    @DeleteMapping("/review")
    public boolean deleteReview(@RequestBody DeleteReviewOrCommentRequest request) {
        return reviewService.deleteReview(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить отзывы на майнор")
    @GetMapping("/review")
    public List<UiReview> getReviews(@RequestParam Long minorId) {
        return reviewService.getReviews(minorId);
    }

    @Operation(summary = "Получить отзывы на майнор, сортированные по дате создания")
    @GetMapping("/review/sortByDate")
    public List<UiReview> getReviewsSortByDate(@RequestParam Long minorId) {
        return reviewService.getReviewsSortByDate(minorId);
    }

    @Operation(summary = "Создать комментарий на майнор")
    @PostMapping("/review/comment")
    public UiComment createComment(@RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @Operation(summary = "Обновить отзыв на майнор")
    @PatchMapping("/review/comment")
    public UiComment updateComment(@RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(request);
    }

    @Operation(summary = "Удалить отзыв на майнор")
    @DeleteMapping("/review/comment")
    public boolean deleteComment(@RequestBody DeleteReviewOrCommentRequest request) {
        return commentService.deleteComment(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить комментарии к отзыву")
    @GetMapping("/review/comment")
    public Set<UiComment> getComments(@RequestParam Long reviewId) {
        return commentService.getComments(reviewId);
    }

    @Operation(summary = "Поставить лайк/дизлайк на отзыв")
    @PostMapping("/review/like")
    public UiLike addLike(@RequestBody AddLikeRequest request) {
        return likeService.addLike(request);
    }

    @Operation(summary = "Удалить лайк/дизлайк с отзыва")
    @DeleteMapping("/review/like")
    public boolean deleteLike(@RequestBody DeleteLikeRequest request) {
        return likeService.deleteLike(request.getId(), request.getEmail());
    }

    @Operation(summary = "Получить все лайки и дизлайки к отзыву")
    @GetMapping("/review/like")
    public Set<UiLike> getLikes(@RequestParam Long reviewId) {
        return likeService.getLikes(reviewId);
    }
}
