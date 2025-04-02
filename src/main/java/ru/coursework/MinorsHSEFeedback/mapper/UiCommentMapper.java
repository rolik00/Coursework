package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;

public interface UiCommentMapper {
    public UiComment apply(Comment comment);
}
