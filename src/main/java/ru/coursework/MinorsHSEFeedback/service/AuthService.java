package ru.coursework.MinorsHSEFeedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.config.JWTService;
import ru.coursework.MinorsHSEFeedback.request.AuthRequest;
import ru.coursework.MinorsHSEFeedback.request.AuthResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
