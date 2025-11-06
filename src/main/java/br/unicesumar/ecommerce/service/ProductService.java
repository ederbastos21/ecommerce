package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> findByNameContainingIgnoreCase(
        String name,
        Sort sort
    ) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll(sort);
        }
        return productRepository.findByNameContainingIgnoreCase(name, sort);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getFirst12Products() {
        return productRepository.findTop12ByOrderByAmmountSoldDesc();
    }

    public List<Product> getOnlyAvailable() {
        return productRepository.findByAvailableQuantityGreaterThan(0);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public void updateStock(Long productId, int quantityPurchased) {
        Product product = findById(productId);
        if (product.getAvailableQuantity() >= quantityPurchased) {
            product.setAvailableQuantity(
                product.getAvailableQuantity() - quantityPurchased
            );
            product.setAmmountSold(
                product.getAmmountSold() + quantityPurchased
            );
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException(
                "Estoque insuficiente para o produto: " + product.getName()
            );
        }
    }

    public List<Product> findRelatedProducts(String category, Long excludeId) {
        System.out.println(
            ">>> findRelatedProducts chamado com categoria=" +
                category +
                " | excluindo ID=" +
                excludeId
        );
        return productRepository.findTop4ByCategoryAndIdNot(
            category,
            excludeId
        );
    }
}
