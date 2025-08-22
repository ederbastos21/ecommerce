package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.service.CategoryService;
import br.unicesumar.ecommerce.service.ProductService;
import org.springframework.data.domain.Sort;
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
    public String index(@RequestParam (required = false) String search, @RequestParam (required = false) String sortParameter, Model model) {
        if (search != null && !search.isEmpty()){

            model.addAttribute("categories", categoryService.getRootCategories());
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
            }
            model.addAttribute("search",search);
            return "products";
        } else {
            model.addAttribute("categories", categoryService.getRootCategories());
            model.addAttribute("products", productService.getAll());
            model.addAttribute("search",search);
            model.addAttribute("sortParameter",sortParameter);
            return "products";
        }
    }
}
