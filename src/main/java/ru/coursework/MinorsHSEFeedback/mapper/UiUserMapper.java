package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;

public interface UiUserMapper {
    UiUser apply(User user);
}
