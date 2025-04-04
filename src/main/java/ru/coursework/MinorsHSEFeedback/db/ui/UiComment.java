package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiComment {
    private Long id;
    private String body;
    private Long reviewId;
    private String userName;
    private LocalDate createDate;
    private Long parentId;
}
