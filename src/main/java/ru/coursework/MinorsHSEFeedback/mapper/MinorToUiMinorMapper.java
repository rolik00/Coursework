package ru.coursework.MinorsHSEFeedback.mapper;

import org.mapstruct.Mapper;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

@Mapper(componentModel ="spring")
public interface MinorToUiMinorMapper {

    public UiMinor apply (Minor minor);
    //@AfterMapping categoryId and ratings
}
