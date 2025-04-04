package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    @NotNull
    private Long reviewId;
    @NotNull
    private String email;
    @NotNull
    private String body;
    private Long parentId;
}
