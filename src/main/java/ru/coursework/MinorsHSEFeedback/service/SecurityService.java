package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.Security;
import ru.coursework.MinorsHSEFeedback.repository.SecurityRepository;

import java.util.Optional;

@Service
public class SecurityService {
    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Security> findByLogin(String login) {
        return securityRepository.findByLogin(login);
    }

    public Security save(Security security) {
        return securityRepository.save(security);
    }

    public boolean authenticate(String login, String password) {
        Optional<Security> security = findByLogin(login);
        return passwordEncoder.matches(password, security.get().getPassword());
    }
}
