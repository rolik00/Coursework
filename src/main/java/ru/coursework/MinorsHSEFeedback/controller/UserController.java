package ru.coursework.MinorsHSEFeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.security.Principal;
import java.util.regex.Pattern;

public class UserController {
    @Autowired
    private UserRepository userRepository;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$");

    @GetMapping("/update-password")
    public String updatePasswordForm() {
        return "update-password";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNewPassword,
                                 Principal principal, Model model) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!newPassword.equals(confirmNewPassword)) {
                model.addAttribute("error", "New passwords do not match");
                return "update-password";
            }

            if(!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                model.addAttribute("error", "Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                return "update-password";
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("error", "Current password is incorrect");
                return "update-password";
            }

            user.setPassword(encodedPassword);
            userRepository.save(user);

            model.addAttribute("message", "Password updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-password";
    }
}
