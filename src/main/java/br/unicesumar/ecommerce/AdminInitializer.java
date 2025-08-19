package br.unicesumar.ecommerce;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final UserService userService;

    public AdminInitializer(UserService userService){
        this.userService = userService;
    }

    @PostConstruct
    public void initAdmin() {
        String adminEmail = "admin@email.com";
        if (userService.findByEmail(adminEmail) == null) {
            User admin = new User();
            admin.setName("admin");
            admin.setAge(123);
            admin.setCpf("123");
            admin.setEmail(adminEmail);
            admin.setAddress("123");
            admin.setPassword("admin");
            admin.setRole("ADMIN");
            userService.saveUser(admin);
            System.out.println("Admin criado: " + adminEmail);
        }
    }

}
