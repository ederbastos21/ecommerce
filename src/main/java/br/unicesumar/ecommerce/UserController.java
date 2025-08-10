package br.unicesumar.ecommerce;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/register")
        public String showRegisterForm(){
        return "register";
    }
    @GetMapping("/success")
        public String showSuccessPage(){
        return "success";
    }
    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute User user, Model model){
        User processedUser = userService.processUser(user);
        model.addAttribute("user",processedUser);
        return "success";
    }
}
