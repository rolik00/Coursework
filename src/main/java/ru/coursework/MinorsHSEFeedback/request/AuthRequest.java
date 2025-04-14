package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
