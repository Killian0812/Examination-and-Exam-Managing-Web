package com.killian.SpringBoot.ExamApp.controllers.viewcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.killian.SpringBoot.ExamApp.models.User;
import com.killian.SpringBoot.ExamApp.repositories.UserRepository;
import com.killian.SpringBoot.ExamApp.services.EmailService;
import com.killian.SpringBoot.ExamApp.services.SessionManagementService;

@Controller
public class UserController {

    @Autowired
    private SessionManagementService sessionManagementService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        User user = userRepository.findByUsername(username);
        String message = null;

        if (user == null) {
            message = "Tên đăng nhập không tồn tại.";
            sessionManagementService.setMessage(message);
            return "redirect:/";
        } else {
            if (password.equals(user.getPassword()) == false) {
                message = "Sai mật khẩu.";
                sessionManagementService.setMessage(message);
                sessionManagementService.setUsername(username);
                return "redirect:/";
            } else {
                sessionManagementService.createUserSession(username, password, user.getRole());
                return "redirect:/" + user.getRole().toLowerCase() + "/dashboard";
            }
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        User user = userRepository.findByUsername(sessionManagementService.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/change-password-page")
    public String changePasswordPage(Model model) {
        model.addAttribute("message", sessionManagementService.getMessage());
        sessionManagementService.clearMessage();
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        String password = sessionManagementService.getPassword();
        if (currentPassword.equals(password)) {
            if (newPassword.equals(confirmPassword)) {
                sessionManagementService.setMessage("Đổi mật khẩu thành công.");
                String username = sessionManagementService.getUsername();
                User user = userRepository.findByUsername(username);
                user.setPassword(newPassword);
                userRepository.save(user);
            } else {
                sessionManagementService.setMessage("Mật khẩu xác nhận không đúng.");
            }
        } else
            sessionManagementService.setMessage("Mật khẩu hiện tại không đúng");
        return "redirect:/change-password-page";
    }

    @PostMapping("/forget")
    public String forget(@RequestParam("email") String email, Model model) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            sessionManagementService.setMessage("Email chưa đăng ký tài khoản.");
            return "redirect:/forget-password";
        }
        try {
            String emailBody = "Tên đăng nhập: " + user.getUsername() + "<br>" + "Mật khẩu: " + user.getPassword();
            emailService.sendEmail(email, "Quên mật khẩu", emailBody);
            sessionManagementService.setMessage("Thông tin đăng nhập đã được gửi về email của bạn.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/forget-password";
    }

    @GetMapping("/back-to-dashboard")
    public String backToDashboard() {
        return "redirect:/" + sessionManagementService.getRole().toLowerCase() + "/dashboard";
    }

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Model model) {
        // Retrieve user data from the session
        String username = sessionManagementService.getUsername();
        String password = sessionManagementService.getPassword();
        String role = sessionManagementService.getRole();

        // Use the data as needed
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("role", role);
        model.addAttribute("message", sessionManagementService.getMessage());
        sessionManagementService.clearMessage();

        return "teacher/teacher-dashboard";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model) {
        // Retrieve user data from the session
        String username = sessionManagementService.getUsername();
        String password = sessionManagementService.getPassword();
        String role = sessionManagementService.getRole();

        // Use the data as needed
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("role", role);
        model.addAttribute("message", sessionManagementService.getMessage());
        sessionManagementService.clearMessage();

        return "student/student-dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        // Clear user session on logout
        sessionManagementService.clearUserSession();
        return "redirect:/";
    }
}