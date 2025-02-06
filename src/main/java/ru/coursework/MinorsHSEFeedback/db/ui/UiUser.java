package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UiUser {
    private Long id;
    private String name;
    private String courseTitle;
    private String minorTitle;
    private String email;
    private float rating;
}
