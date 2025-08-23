package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.UserRepository;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
}
