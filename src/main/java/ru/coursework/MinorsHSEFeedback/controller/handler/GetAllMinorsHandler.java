package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllMinorsHandler {
    private final MinorService minorService;
    private final UiMinorMapper minorMapper;

    @Transactional(readOnly = true)
    public List<UiMinor> handle() {
        return minorService.findAllMinors().stream().map(minorMapper::apply).toList();
    }
}
