package ru.coursework.MinorsHSEFeedback.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotNull
    private String name;
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String courseTitle;
    @NotNull
    private String minorTitle;
}
