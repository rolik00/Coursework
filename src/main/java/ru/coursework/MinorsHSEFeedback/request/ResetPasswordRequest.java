package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotNull
    private String token;
    @NotNull
    private String password;
    @NotNull
    private String confirmPassword;
}
