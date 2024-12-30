package ru.coursework.MinorsHSEFeedback.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    private String login;
    @NotNull
    private String password;
}
