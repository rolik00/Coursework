package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllSortedMinorsHandler {

    private final MinorService minorService;
    private final UiMinorMapper minorMapper;

    @Transactional(readOnly = true)
    public List<UiMinor> handle(Comparator<UiMinor> comparator) {
        List<Minor> minors = minorService.findAllMinors();
        List<UiMinor> uiMinors = new ArrayList<>(minors.stream().map(minorMapper::apply).toList());
        uiMinors.sort(comparator);
        return uiMinors;
    }
}
