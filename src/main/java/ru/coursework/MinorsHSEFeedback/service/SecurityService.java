package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.Security;
import ru.coursework.MinorsHSEFeedback.repository.SecurityRepository;

import java.util.Optional;

@Service
public class SecurityService {
    @Autowired
    private SecurityRepository securityRepository;

    public Optional<Security> findByLogin(String login) {
        return securityRepository.findByLogin(login);
    }

    public Security save(Security security) {
        return securityRepository.save(security);
    }
}
