package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Purchase;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.UserRepository;
import br.unicesumar.ecommerce.service.PurchaseService;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //register
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(
        @ModelAttribute User user,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        user.setRole("USER");
        int number = (int) (Math.random() * 10000);
        user.setToken(number);
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute(
                "emailAlreadyRegisteredError",
                "Já existe uma conta com esse email"
            );
            return "register";
        }

        redirectAttributes.addFlashAttribute("successToken", number);

        userService.saveUser(user);
        return "redirect:/login";
    }

    //login
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLoginPage(
        @RequestParam String email,
        @RequestParam String password,
        HttpSession session,
        Model model
    ) {
        User user = userService.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedUser", user); // guarda na sessão
            return "redirect:/"; // volta para home já logado
        }

        model.addAttribute("loginNotFound", "Email ou senha inválidos");
        return "login";
    }

    //logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    //account management functions
    @GetMapping("/edit/{id}")
    public String editUserForm(
        @PathVariable Long id,
        Model model,
        HttpSession session
    ) {
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
    public String saveUser(@ModelAttribute User userForm, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (
            loggedUser == null || !loggedUser.getId().equals(userForm.getId())
        ) {
            return "redirect:/userProfile";
        }

        // busca o usuário completo no banco
        User userToUpdate = userService.getById(userForm.getId());

        // atualiza apenas os campos do formulário
        userToUpdate.setName(userForm.getName());
        userToUpdate.setEmail(userForm.getEmail());
        userToUpdate.setAge(userForm.getAge());

        // se não for admin, força role para USER
        if (!"ADMIN".equals(loggedUser.getRole())) {
            userToUpdate.setRole("USER");
        }

        User savedUser = userService.saveUser(userToUpdate);

        session.setAttribute("loggedUser", savedUser);

        return "redirect:/userProfile";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
        @PathVariable Long id,
        @ModelAttribute User user,
        HttpSession session
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !loggedUser.getId().equals(user.getId())) {
            return "redirect:/userProfile";
        }
        userService.deleteById(id);
        session.invalidate();
        return "redirect:/";
    }

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/purchaseHistory")
    public String purchaseHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Purchase> purchases = purchaseService.getUserPurchasesById(
            user.getId()
        );
        System.out.println("User ID: " + user.getId());
        System.out.println("Number of purchases found: " + purchases.size());

        model.addAttribute("purchases", purchases);
        model.addAttribute("user", user);

        return "purchaseHistory";
    }

    @GetMapping("/passwordChange")
    public String returnPasswordChangePage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/passwordReset";
        }
        model.addAttribute("loggedUser", loggedUser);
        return "passwordChangeLogged";
    }

    @GetMapping("/passwordReset")
    public String returnPasswordResetPage(Model model) {
        return "passwordReset";
    }

    @PostMapping("/changePasswordAnon")
    public String changePasswordAnon(
        @RequestParam String email,
        @RequestParam String token,
        @RequestParam String newPassword,
        Model model,
        HttpSession session
    ) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute(
                "error",
                "email nao corresponde a uma conta existente"
            );
            return "passwordReset";
        }

        boolean canPass = true;
        Date dateNow = new Date();
        Date attemptTime = user.getLastAttemptDate();

        if (attemptTime != null) {
            long differenceMilliseconds =
                dateNow.getTime() - attemptTime.getTime();
            long fifteenMinutes = 15 * 60 * 1000;
            if (
                differenceMilliseconds < fifteenMinutes ||
                user.getFailedAttempts() >= 3
            ) {
                canPass = false;
            }

            if (
                differenceMilliseconds > fifteenMinutes &&
                user.getFailedAttempts() >= 3
            ) {
                canPass = true;
                user.setFailedAttempts(0);
                userService.saveUser(user);
            }
        }

        if (!canPass) {
            model.addAttribute(
                "error",
                "Bloqueado por excesso de tentativas, tente novamente em 15 minutos"
            );
            return "passwordReset";
        }

        if (Integer.toString(user.getToken()).equals(token)) {
            user.setPassword(newPassword);
            user.setLastAttemptDate(null);
            user.setFailedAttempts(0);
            userService.saveUser(user);
            model.addAttribute("success", "Senha trocada com sucesso!");
            return "passwordReset";
        } else {
            int failedAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(failedAttempts);
            String message = (3 -
                (user.getFailedAttempts()) +
                " tentativas restantes");
            model.addAttribute("error", message);
            if (user.getFailedAttempts() >= 3) {
                user.setLastAttemptDate(new Date());
                model.addAttribute(
                    "error",
                    "Bloqueado por excesso de tentativas, tente novamente em 15 minutos"
                );
            }
            userService.saveUser(user);
            return "passwordReset";
        }
    }

    @PostMapping("/changePasswordLogged")
    public String changePasswordLogged(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String oldPassword,
        @RequestParam String newPassword,
        Model model,
        HttpSession session
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/passwordReset";
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute(
                "error",
                "email nao corresponde a uma conta existente"
            );
            model.addAttribute("loggedUser", loggedUser);
            return "passwordChangeLogged";
        }

        if (user.getPassword().equals(oldPassword)) {
            loggedUser.setToken(user.getToken());
            loggedUser.setEmail(user.getEmail());
            user.setPassword(newPassword);
            userService.saveUser(user);
            session.invalidate();
            model.addAttribute("success", "senha alterada com sucesso");
            model.addAttribute("justChangedPassword", ".");
            return "login";
        } else {
            model.addAttribute("error", "email ou senha errados");
            model.addAttribute("loggedUser", loggedUser);
            return "passwordChangeLogged";
        }
    }
}
