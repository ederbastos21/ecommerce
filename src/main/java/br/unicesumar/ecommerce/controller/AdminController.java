package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.service.UserService;
import br.unicesumar.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    //admin home loader
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

    //======================== USERS =========================

    //show user table
    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeTable", "users");
        return "admin";
    }

    //show form to create a new user
    @GetMapping("/users/new")
    public String newUserForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        model.addAttribute("activeTable", "users");
        return "userForm";
    }

    //show form to edit an existing user's info
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
                return "redirect:/";
            }
            User user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("activeTable", "users");
            return "userForm";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    //saves the user in the db
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    //removes a user from the db
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    //======================== PRODUCTS =========================

    //show products table
    @GetMapping("/products")
    public String listProducts(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("products", productService.getAll());
        model.addAttribute("activeTable", "products");
        return "admin";
    }

    //show form to create a new product
    @GetMapping("/products/new")
    public String newProductForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("product", new Product());
        model.addAttribute("activeTable", "products");
        return "productForm";
    }

    //show form to edit an existing product
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
                return "redirect:/";
            }
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("activeTable", "products");
            return "productForm";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }

    //saves the product in the db
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    //removes a product from the db
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        productService.deleteById(id);
        return "redirect:/admin/products";
    }
}