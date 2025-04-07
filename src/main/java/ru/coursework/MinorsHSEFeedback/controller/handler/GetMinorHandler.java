package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

@Component
@RequiredArgsConstructor
public class GetMinorHandler {
    private final MinorService minorService;
    private final UiMinorMapper minorMapper;

    @Transactional(readOnly = true)
    public UiMinor handle(Long id) {
        return minorMapper.apply(minorService.getMinor(id));
    }
}
