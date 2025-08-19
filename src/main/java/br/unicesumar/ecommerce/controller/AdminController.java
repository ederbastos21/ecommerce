package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.UserService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            // não é admin → redireciona pro index
            return "redirect:/";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin"; // nome do template
    }
}
