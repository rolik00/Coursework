package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.mapper.UiLikeMapper;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

@Component
public class UiLikeMapperImpl implements UiLikeMapper {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UiLike apply(Like like) {
        if (like == null) {
            return null;
        }
        String email = userRepository.findById(like.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")).getEmail();
        UiLike uiLike = new UiLike();
        uiLike.setId(like.getId());
        uiLike.setValue(like.isValue());
        uiLike.setReviewId(like.getReviewId());
        uiLike.setEmail(email);
        return uiLike;
    }
}
