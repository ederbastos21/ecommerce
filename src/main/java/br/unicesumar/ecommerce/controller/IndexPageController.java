package br.unicesumar.ecommerce.controller;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import br.unicesumar.ecommerce.service.ProductService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexPageController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public IndexPageController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("role", loggedUser.getRole());
        }

        model.addAttribute("categories", categoryService.getRootCategories());
        model.addAttribute("products", productService.getAll());

        return "index";
    }

    @GetMapping("/userProfile")
    public String showUserProfile(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        model.addAttribute("loggedUser",loggedUser);
        return "userProfile";
    }

}


