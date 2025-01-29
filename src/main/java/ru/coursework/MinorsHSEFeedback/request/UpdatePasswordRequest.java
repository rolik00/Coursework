package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmNewPassword;
}

