package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.UserRepository;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/productDetail/{id}")
    public String ShowProductDetail(Model model, @PathVariable Long id){
        Product product = productService.findById(id);
        model.addAttribute("product",product);
        return "productDetail";
    }

    @PostMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session){
        Product product = productService.findById(id);
        List<Product> cart = (List<Product>) session.getAttribute("cart");

        if (cart == null){
            cart = new ArrayList<>();
        }

        cart.add(product);
        session.setAttribute("cart",cart);

        return "redirect:/productDetail/{id}";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model){
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cart", cart);
        return "cart";
    }
}
