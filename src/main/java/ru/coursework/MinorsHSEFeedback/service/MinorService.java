package ru.coursework.MinorsHSEFeedback.service;

import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.MinorTitleInfo;

import java.util.List;
import java.util.Set;

public interface MinorService {
    Minor getMinor(Long id);
    List<Minor> findAllMinors();
    List<Minor> findAllMinorsByCategoryIds(Set<Long> categoryIds);
    List<Minor> getMinorsByIds(Set<Long> ids);
    Long getMinorIdByTitle(String title);
    List<MinorTitleInfo> getMinorTitleInfo();
}
