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
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}

