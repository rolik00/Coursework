package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUsersHandler {
    private final UserService userService;
    private final UiUserMapper userMapper;
    private final Comparator<UiUser> comparatorByRating = (u1, u2) -> Float.compare(u2.getRating(), u1.getRating());

    @Transactional(readOnly = true)
    public List<UiUser> handle() {
        List<UiUser> uiUsers =new ArrayList<>(userService.findAll().stream().map(userMapper::apply).toList());
        uiUsers.sort(comparatorByRating);
        return uiUsers;
    }

}
