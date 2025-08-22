package br.unicesumar.ecommerce.service;
import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> findByNameContainingIgnoreCase(String name, Sort sort){
        return productRepository.findByNameContainingIgnoreCase(name, sort);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}

