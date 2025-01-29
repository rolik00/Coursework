package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.coursework.MinorsHSEFeedback.request.RegistrationRequest;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.request.UpdatePasswordRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.security.Principal;
import java.util.regex.Pattern;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.*;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.*;

@Controller
@Slf4j
public class AuthController {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailSender emailSender;

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$");
	private static final String endLetter = "С уважением,\nКоманда MinorsHSEFeedbacks";

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}

	@Operation(summary = "Регистрация нового пользователя")
	@PostMapping("/process_register")
	public String processRegister(/*@RequestBody*/ @ModelAttribute RegistrationRequest request, Model model) {
		log.info("invoke processRegister");
		if (userService.findByEmail(request.getEmail()).isPresent()) {
			log.error("User = {}, error = {} ", request.getEmail(), IS_EXIST_ERROR.getTitle());
			model.addAttribute("error", IS_EXIST_ERROR.getTitle());
			return "error";
		}

		if (!request.getEmail().endsWith("@edu.hse.ru")) {
			log.error("User = {}, error = {} ", request.getEmail(), IS_NOT_HSE_ERROR.getTitle());
			model.addAttribute("error", IS_NOT_HSE_ERROR.getTitle());
			return "error";
		}

		if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
			log.error("User = {}, error = {} ", request.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
			model.addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
			return "error";
		}

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			log.error("User = {}, error = {} ", request.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
			model.addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
			return "error";
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setMinorTitle(request.getMinorTitle());
		user.setCourseTitle(request.getCourseTitle());
		user.setPassword(encodedPassword);
		userService.save(user);

		String content = BEGIN.getTitle() + user.getName() + REGISTRATION.getTitle() + END.getTitle();
		emailSender.sendEmail(user.getEmail(), "Регистрация на портале MinorsHSEFeedbacks", content);
		log.info("Пользователь {} успешно зарегистрирован!", user.getEmail());
		return "register_success";
	}

	@GetMapping("/update_password")
	public String updatePasswordForm() {
		return "update_password";
	}

	@Operation(summary = "Обновление пароля")
	@PostMapping("/update_password")
	public String updatePassword(/*@RequestBody*/ @ModelAttribute UpdatePasswordRequest request,
								 Principal principal, Model model) {
		log.info("invoke updatePassword");
		try {
			User user = userService.findByEmail(principal.getName())
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(request.getNewPassword());

			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), UNCORRECT_PASSWORD_ERROR.getTitle());
				model.addAttribute("error", UNCORRECT_PASSWORD_ERROR.getTitle());
				return "update_password";
			}

			if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), PASSWORD_MATCH_ERROR.getTitle());
				model.addAttribute("error", PASSWORD_MATCH_ERROR.getTitle());
				return "update_password";
			}

			if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
				model.addAttribute("error", PASSWORD_NOT_MATCH_ERROR.getTitle());
				return "update_password";
			}

			if(!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
				log.error("User = {}, error = {} ", user.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
				model.addAttribute("error", UNRELIABLE_PASSWORD_ERROR.getTitle());
				return "update_password";
			}

			user.setPassword(encodedPassword);
			userService.save(user);

			String content = BEGIN.getTitle() + user.getName() + UPDATE_PASSWORD.getTitle() + END.getTitle();
			emailSender.sendEmail(user.getEmail(), "Обновление пароля на портале MinorsHSEFeedbacks", content);
			log.info("Пользователь {} изменил пароль", user.getEmail());

			model.addAttribute("message", "Password updated successfully");
		} catch (Exception e) {
			log.error("При попытке смены пароля для пользователя {} произошла ошибка {}", principal.getName(), e.getMessage());
			model.addAttribute("error", e.getMessage());
		}
		return "update_password";
	}
}
