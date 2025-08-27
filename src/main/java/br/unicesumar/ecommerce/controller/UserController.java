package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLoginPage(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedUser", user); // guarda na sessão
            return "redirect:/"; // volta para home já logado
        }

        model.addAttribute("loginNotFound", "Email ou senha inválidos");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute User user, Model model) {
        user.setRole("USER");
        if (userService.findByEmail(user.getEmail()) != null ){
            model.addAttribute("emailAlreadyRegisteredError", "Já existe uma conta com esse email");
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (!(loggedUser.getId().equals(id))) {
                return "redirect:/userProfile";
            }
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("activeTable", "users");
            return "editForm";
        } catch (Exception e) {
            return "redirect:/userProfile";
        }
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, HttpSession session) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !loggedUser.getId().equals(user.getId())) {
            return "redirect:/userProfile";
        }

        if (!"ADMIN".equals(loggedUser.getRole())) {
            user.setRole("USER");
        }

        User savedUser = userService.saveUser(user);

        session.setAttribute("loggedUser", savedUser);

        return "redirect:/userProfile";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, @ModelAttribute User user, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !loggedUser.getId().equals(user.getId())) {
            return "redirect:/userProfile";
        }
        userService.deleteById(id);
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/buy")
    public String buy(HttpSession session){
        session.removeAttribute("cart");
        return "redirect:/cart";
    }
}
