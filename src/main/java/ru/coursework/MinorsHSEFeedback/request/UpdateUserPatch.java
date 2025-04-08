package ru.coursework.MinorsHSEFeedback.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPatch {
    private String name;
    private String minorTitle;
    private String email;
    private String courseTitle;
}