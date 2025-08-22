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
    public String index(@RequestParam (required = false) String search, @RequestParam (required = false) String sort, Model model) {
        if (search != null && !search.isEmpty()){
            model.addAttribute("categories", categoryService.getRootCategories());
            model.addAttribute("products",productService.findByNameContainingIgnoreCase(search));
            model.addAttribute("search",search);
            model.addAttribute("sort",sort);
            return "products";
        } else {
            model.addAttribute("categories", categoryService.getRootCategories());
            model.addAttribute("products", productService.getAll());
            model.addAttribute("search",search);
            model.addAttribute("sort",sort);
            return "products";
        }
    }
}
