package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

public interface UiMinorMapper {
    public UiMinor apply(Minor minor);
}
