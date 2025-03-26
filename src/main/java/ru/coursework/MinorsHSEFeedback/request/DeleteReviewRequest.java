package ru.coursework.MinorsHSEFeedback.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReviewRequest {
    private Long reviewId;
    private String email;
}
