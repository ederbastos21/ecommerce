package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Category;
import br.unicesumar.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/filtros")
    public String showFilters(Model model) {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        model.addAttribute("categories", rootCategories);
        return "filters"; // nome do template Thymeleaf filters.html
    }
}

