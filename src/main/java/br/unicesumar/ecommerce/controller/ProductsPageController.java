package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.service.CategoryService;
import br.unicesumar.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductsPageController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductsPageController (CategoryService categoryService, ProductService productService){
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/products")
    public String index(@RequestParam (required = false) String search, Model model) {
        if (search != null && !search.isEmpty()){
            model.addAttribute("products",productService.findByNameContainingIgnoreCase(search));
            return "products";
        } else {
            model.addAttribute("categories", categoryService.getRootCategories());
            model.addAttribute("products", productService.getAll());
            return "products";
        }
    }
}
