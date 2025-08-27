package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
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

    //home loader
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("role", loggedUser.getRole());
        }

        model.addAttribute("products", productService.getFirst12Products());
        return "index";
    }

    //=============== PRODUCTS ===============

    //shows products in products page using sort
    @GetMapping("/products")
    public String index(@RequestParam (required = false) String search, @RequestParam (required = false) String sortParameter, Model model) {
        model.addAttribute("sortParameter",sortParameter);
        if (sortParameter == null){
            sortParameter = "relevance";
        }
        if (sortParameter.equals("priceAsc")) {
            Sort sort = Sort.by("price").ascending();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        } else if (sortParameter.equals("priceDesc")) {
            Sort sort = Sort.by("price").descending();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        } else if (sortParameter.equals("relevance")) {
            Sort sort = Sort.by("ammountSold").descending();
            model.addAttribute("products",productService.findByNameContainingIgnoreCase(search, sort));
        } else {
            Sort sort = Sort.unsorted();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        }
        model.addAttribute("search",search);
        return "products";
    }

    //shows product detail individual page
    @GetMapping("/productDetail/{id}")
    public String ShowProductDetail(Model model, @PathVariable Long id){
        Product product = productService.findById(id);
        model.addAttribute("product",product);
        return "productDetail";
    }


    //=============== CART ===============

    //shows cart page
    @GetMapping("/cart")
    public String cart(HttpSession session, Model model){
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cart", cart);
        return "cart";
    }

    //adds item to cart
    @PostMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session, Model model){
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            model.addAttribute("userNotLoggedError", "Favor logar para acessar o carrinho");
            return"/login";
        }

        Product product = productService.findById(id);
        List<Product> cart = (List<Product>) session.getAttribute("cart");

        if (cart == null){
            cart = new ArrayList<>();
        }

        cart.add(product);
        session.setAttribute("cart",cart);

        return "redirect:/productDetail/{id}";
    }

}
