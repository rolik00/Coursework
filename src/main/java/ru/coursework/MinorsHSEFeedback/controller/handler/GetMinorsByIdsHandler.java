package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GetMinorsByIdsHandler {
    private final MinorService minorService;
    private final UiMinorMapper minorMapper;

    @Transactional(readOnly = true)
    public List<UiMinor> handle(Set<Long> ids) {
        return minorService.getMinorsByIds(ids).stream().map(minorMapper::apply).toList();
    }
}
