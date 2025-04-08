package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

@Component
@RequiredArgsConstructor
public class UiUserMapperImpl implements UiUserMapper {
    private final MinorRepository minorRepository;
    @Override
    public UiUser apply(User user) {
        if (user == null) {
            return null;
        }
        String minorTitle = minorRepository.findById(user.getMinorId()).orElseThrow().getTitle();

        UiUser uiUser = new UiUser();
        uiUser.setId(user.getId());
        uiUser.setName(user.getName());
        uiUser.setCourseTitle(user.getCourseTitle());
        uiUser.setMinorTitle(minorTitle);
        uiUser.setEmail(user.getEmail());
        uiUser.setRating(user.getRating());

        return uiUser;
    }
}

