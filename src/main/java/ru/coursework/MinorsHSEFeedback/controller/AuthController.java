package ru.coursework.MinorsHSEFeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.security.Principal;
import java.util.regex.Pattern;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.*;

@Controller
public class AuthController {

	/*@Autowired
	private UserRepository userRepository;*/
	@Autowired
	private UserService userService;
	@Autowired
	private EmailSender emailSender;

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$");

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user, Model model) {
		if (userService.findByEmail(user.getEmail()).isPresent()) {
			model.addAttribute("error", IS_EXIST_ERROR.getTitle());
			return "error";
		}

		if (!user.getEmail().endsWith("@edu.hse.ru")) {
			model.addAttribute("error", IS_NOT_HSE_ERROR.getTitle());
			return "error";
		}

		if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
			model.addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
			return "error";
		}

		//TODO: проверка что password1 == password2

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		userService.save(user);

		emailSender.sendEmail(user.getEmail(), "Регистрация на портале MinorsHSEFeedbacks", "Здравствуйте, " + user.getName() + "!\n\n\nСообщаем Вам, что вы успешно зарегистрировали на портале MinorsHSEFeedbacks");
		
		return "register_success";
	}

	@GetMapping("/update_password")
	public String updatePasswordForm() {
		return "update_password";
	}

	@PostMapping("/update_password")
	public String updatePassword(@RequestParam String currentPassword,
								 @RequestParam String newPassword,
								 @RequestParam String confirmNewPassword,
								 Principal principal, Model model) {
		try {
			User user = userService.findByEmail(principal.getName())
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			if (!newPassword.equals(confirmNewPassword)) {
				model.addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
				return "update_password";
			}

			if(!PASSWORD_PATTERN.matcher(newPassword).matches()) {
				model.addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
				return "update_password";
			}

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(newPassword);
			if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
				model.addAttribute("error", UNCORRECT_PASSWORD_ERROR.getTitle());
				return "update_password";
			}

			user.setPassword(encodedPassword);
			userService.save(user);

			model.addAttribute("message", "Password updated successfully");
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "update_password";
	}
}
