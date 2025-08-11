package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.Category;
import br.unicesumar.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }
}

