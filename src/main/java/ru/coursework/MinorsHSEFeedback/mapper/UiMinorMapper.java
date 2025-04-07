package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

public interface UiMinorMapper {
    UiMinor apply(Minor minor);
}
