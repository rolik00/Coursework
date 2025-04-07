package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.mapper.UiReviewMapper;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UiReviewMapperImpl implements UiReviewMapper {
    private final UserRepository userRepository;
    private final MinorRepository minorRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    @Override
    public UiReview apply(Review review) {
        if (review == null) {
            return null;
        }
        String userName = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")).getName();
        String title = minorRepository.findById(review.getMinorId()).orElseThrow().getTitle();
        UiReview uiReview = new UiReview();
        uiReview.setId(review.getId());
        uiReview.setUserName(userName);
        uiReview.setMinorTitle(title);
        uiReview.setBody(review.getBody());
        uiReview.setDifficultyMark(review.getDifficultyMark());
        uiReview.setInterestMark(review.getInterestMark());
        uiReview.setTimeConsumptionMark(review.getTimeConsumptionMark());
        uiReview.setTotalMark(review.getTotalMark());
        uiReview.setCreateDate(review.getCreateDate());
        uiReview.setCommentsCount(commentRepository.countCommentsByReviewIds(Set.of(review.getId())));
        uiReview.setLikesCount(likeRepository.getCountLikesByReviewIds(Set.of(review.getId())));
        uiReview.setDislikesCount(likeRepository.getCountDislikesByReviewIds(Set.of(review.getId())));
        uiReview.setValue(review.getValue());
        return uiReview;
    }
}
