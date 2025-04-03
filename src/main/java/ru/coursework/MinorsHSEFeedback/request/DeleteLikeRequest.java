package ru.coursework.MinorsHSEFeedback.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteLikeRequest {
    private Long id;
    private String email;
}
