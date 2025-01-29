package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String courseTitle;
    @NotNull
    private String minorTitle;
    @NotNull
    private String password;
    @NotNull
    private String confirmPassword;
}
