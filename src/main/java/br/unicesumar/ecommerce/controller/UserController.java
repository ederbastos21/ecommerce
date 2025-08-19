package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /* LOGIN */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLoginPage(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   Model model) {
        User user = userService.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedUser", user); // guarda na sessão
            return "redirect:/"; // volta para home já logado
        }

        model.addAttribute("error", "Email ou senha inválidos");
        return "login";
    }

    /* LOGOUT */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /* REGISTER */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute User user, Model model) {
        user.setRole("user"); // padrão
        User processedUser = userService.saveUser(user);
        model.addAttribute("user", processedUser);
        return "successRegister";
    }
}
