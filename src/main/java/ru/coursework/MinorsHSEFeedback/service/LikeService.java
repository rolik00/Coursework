package ru.coursework.MinorsHSEFeedback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.exceptions.LikeException;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.mapper.UiLikeMapper;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UiLikeMapper likeMapper;

    @Transactional
    public UiLike addLike(AddLikeRequest request) {
        checkCanAddLike(request.getEmail(), request.getReviewId());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Like like = new Like();
        like.setValue(request.isValue());
        like.setReviewId(request.getReviewId());
        like.setUserId(user.getId());
        likeRepository.save(like);
        return likeMapper.apply(like);
    }

    @Transactional
    public boolean deleteLike(Long id, String email) {
        checkCanDeleteLike(id, email);
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeException("Like not found"));
        likeRepository.delete(like);
        return true;
    }

    @Transactional(readOnly = true)
    public Set<UiLike> getLikes(Long reviewId) {
        Set<Like> likes = likeRepository.getLikes(reviewId);
        return likes.stream().map(likeMapper::apply).collect(Collectors.toSet());
    }

    private void checkCanAddLike(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("Review not found"));
        Optional<Like> like = likeRepository.findLikeByUserIdAndReviewId(user.getId(), reviewId);
        if (like.isPresent()) {
            throw new LikeException("Пользователь уже поставил лайк или дизлайк на этот отзыв");
        }
        if (user.getId().equals(review.getUserId())) {
            throw new LikeException("Пользователь не может поставить лайк/дизлайк на свой отзыв");
        }
    }

    private void checkCanDeleteLike(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeException("Like not found"));
        if(!like.getUserId().equals(user.getId())) {
            throw new LikeException("Текущий пользователь не ставил лайк на этот отзыв");
        }
    }
}
