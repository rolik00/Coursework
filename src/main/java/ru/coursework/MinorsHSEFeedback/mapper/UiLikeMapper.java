package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;

public interface UiLikeMapper {
    public UiLike apply(Like like);
}
