package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.service.UserService;
import br.unicesumar.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping
    public String adminHome(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeTable", "users");
        return "admin";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeTable", "users");
        return "admin";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("activeTable", "users");
        return "userForm";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("activeTable", "users");
            return "userForm";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAll());
        model.addAttribute("activeTable", "products");
        return "admin";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("activeTable", "products");
        return "productForm";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("activeTable", "products");
            return "productForm";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/products";
    }
}