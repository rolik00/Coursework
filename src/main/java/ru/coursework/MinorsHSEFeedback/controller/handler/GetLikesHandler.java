package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.mapper.UiLikeMapper;
import ru.coursework.MinorsHSEFeedback.service.LikeService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetLikesHandler {
    private final LikeService likeService;
    private final UiLikeMapper likeMapper;

    @Transactional(readOnly = true)
    public Set<UiLike> handle(Long reviewId) {
        return likeService.getLikes(reviewId).stream().map(likeMapper::apply).collect(Collectors.toSet());
    }
}
