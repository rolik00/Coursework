package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiLike {
    private Long id;
    private boolean value;
    private Long reviewId;
    private String email;
}
