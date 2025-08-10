package br.unicesumar.ecommerce;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /*LOGIN*/

    @GetMapping("/login")
        public String showLoginPage(){
        return"login";
    }

    @PostMapping ("/login")
    public String processLoginPage(@RequestParam String email, @RequestParam String password, Model model){
        User user = userService.findByEmail(email);
        if (user != null && user.getPassword().equals(password)){
            model.addAttribute("user", user);
            return "successLogin";
        }
        model.addAttribute("error");
        return "login";
    }

    /*REGISTER*/

    @GetMapping("/register")
    public String showRegisterForm(){
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute User user, Model model){
        User processedUser = userService.saveUser(user);
        model.addAttribute("user",processedUser);
        return "successRegister";
    }
}
