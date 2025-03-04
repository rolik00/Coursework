package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;

@Component
public class UiUserMapperImpl implements UiUserMapper {
    private static final String BASE_URL = "/user/";
    @Override
    public UiUser apply(User user) {
        if (user == null) {
            return null;
        }

        UiUser uiUser = new UiUser();
        uiUser.setId(user.getId());
        uiUser.setName(user.getName());
        uiUser.setCourseTitle(user.getCourseTitle());
        uiUser.setMinorTitle(user.getMinorTitle());
        uiUser.setEmail(user.getEmail());
        uiUser.setRating(user.getRating());
        uiUser.setSelfLink(BASE_URL + user.getId());

        return uiUser;
    }
}

