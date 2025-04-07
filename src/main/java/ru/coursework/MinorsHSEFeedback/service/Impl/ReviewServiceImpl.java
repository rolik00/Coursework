package ru.coursework.MinorsHSEFeedback.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ResultRepository resultRepository;
    private final MinorRepository minorRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Long minorId = minorRepository.findByTitle(request.getMinorTitle());
        checkCanCreateReview(request.getEmail(), minorId);

        Review review = Review.builder()
                .userId(user.getId())
                .minorId(minorId)
                .body(request.getBody())
                .difficultyMark(request.getDifficultyMark())
                .interestMark(request.getInterestMark())
                .timeConsumptionMark(request.getTimeConsumptionMark())
                .totalMark(request.getTotalMark())
                .createDate(LocalDate.now())
                .build();
        reviewRepository.save(review);

        user.setCount(user.getCount() + 1);
        userRepository.save(user);

        Result result = resultRepository.findByMinorId(minorId).orElseThrow();
        result.setReviewsCount(result.getReviewsCount() + 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() + request.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() + request.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() + request.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() + request.getTotalMark());
        resultRepository.save(result);

        return review;
    }

    @Override
    @Transactional
    public Review updateReview(UpdateReviewRequest request) {
        checkCanUpdateOrDeleteReview(request.getReviewId(), request.getEmail());
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ReviewException("Отзыв не найден"));
        Result result = resultRepository.findByMinorId(review.getMinorId()).orElseThrow();
        if (request.getPatch().getBody() != null) {
            review.setBody(request.getPatch().getBody());
        }
        if (request.getPatch().getDifficultyMark() != null) {
            int tmp = review.getDifficultyMark();
            review.setDifficultyMark(request.getPatch().getDifficultyMark());
            result.setDifficultyMarkSum(result.getDifficultyMarkSum() - tmp + request.getPatch().getDifficultyMark());
        }
        if (request.getPatch().getInterestMark() != null) {
            int tmp = review.getInterestMark();
            review.setInterestMark(request.getPatch().getInterestMark());
            result.setInterestMarkSum(result.getInterestMarkSum() - tmp + request.getPatch().getInterestMark());
        }
        if (request.getPatch().getTimeConsumptionMark() != null) {
            int tmp = review.getTimeConsumptionMark();
            review.setTimeConsumptionMark(request.getPatch().getTimeConsumptionMark());
            result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() - tmp + request.getPatch().getTimeConsumptionMark());
        }
        if (request.getPatch().getTotalMark() != null) {
            int tmp = review.getTotalMark();
            review.setTotalMark(request.getPatch().getTotalMark());
            result.setTotalMarkSum(result.getTotalMarkSum() - tmp + request.getPatch().getTotalMark());
        }
        reviewRepository.save(review);
        resultRepository.save(result);
        return review;
    }

    @Override
    @Transactional
    public boolean deleteReview(Long id, String email) {
        checkCanUpdateOrDeleteReview(id, email);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Отзыв не найден"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Result result = resultRepository.findByMinorId(review.getMinorId()).orElseThrow();
        user.setCount(user.getCount() - 1);
        result.setReviewsCount(result.getReviewsCount() - 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() - review.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() - review.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() - review.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() - review.getTotalMark());
        userRepository.save(user);
        resultRepository.save(result);
        reviewRepository.delete(review);
        likeRepository.deleteByReviewId(review.getId());
        commentRepository.deleteByReviewId(review.getId());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Review> getReviews(Long minorId) {
        Set<Review> reviews = reviewRepository.getReviews(minorId);
        Set<Long> reviewIds = reviews.stream().map(Review::getId).collect(Collectors.toSet());
        reviews.forEach(review -> review.setValue(setValue(review.getId(), reviewIds)));
        return reviews;
    }

    private void checkCanCreateReview(String email, Long minorId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Optional<Review> review = reviewRepository.findReviewByUserIdAndMinorId(user.getId(), minorId);
        if (review.isPresent()) {
            throw new ReviewException("Пользователь уже писал отзыв для этого майнора");
        }
        if (user.getCount() >= 4) {
            throw new ReviewException("Превышен лимит количества отзывов для пользователя");
        }
    }

    private void checkCanUpdateOrDeleteReview(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewException("Отзыв не найден"));
        if(!review.getUserId().equals(user.getId())) {
            throw new ReviewException("Текущий пользователь не является создателем данного отзыва");
        }
    }

    private float setValue(Long reviewId, Set<Long> reviewIds) {
        return setCommentValue(reviewId, reviewIds) + setLikeValue(reviewId, reviewIds);
    }

    private float setCommentValue(Long reviewId, Set<Long> reviewIds) {
        int tmpComment = commentRepository.countCommentsByReviewIds(Set.of(reviewId));
        int totalComment = commentRepository.countCommentsByReviewIds(reviewIds);
        if (tmpComment == 0 || totalComment == 0) return 0;
        return (float) tmpComment / totalComment;
    }

    private float setLikeValue(Long reviewId, Set<Long> reviewIds) {
        int totalLikes = likeRepository.getCountLikesByReviewIds(reviewIds);
        int totalDislikes = likeRepository.getCountDislikesByReviewIds(reviewIds);
        int tmpLikes = likeRepository.getCountLikesByReviewIds(Set.of(reviewId));
        int tmpDislikes = likeRepository.getCountDislikesByReviewIds(Set.of(reviewId));
        int max = totalLikes - totalDislikes;
        if ((tmpLikes - tmpDislikes) == 0) return 0;
        if (max > 0) {
            return (float) (tmpLikes - tmpDislikes) / max;
        } else if (max == 0) {
            return (float) 1 / (tmpLikes - tmpDislikes);
        }
        return (float) -(tmpLikes - tmpDislikes) / max;
    }
}
