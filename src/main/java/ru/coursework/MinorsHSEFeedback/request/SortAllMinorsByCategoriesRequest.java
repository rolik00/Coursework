package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortAllMinorsByCategoriesRequest {
    private Set<@NotNull Long> categoryIds;
    @NotNull
    private String comparator;
}
