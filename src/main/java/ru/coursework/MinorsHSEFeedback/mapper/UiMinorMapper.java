package ru.coursework.MinorsHSEFeedback.mapper;

import org.mapstruct.Mapper;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.UiMinor;

import java.util.function.Function;

public interface UiMinorMapper {
    public UiMinor apply(Minor minor);
}
