package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    Optional<Product> findById(Long id);
    List<Product> findTop12ByOrderByAmmountSoldDesc();
    List<Product> findByAvailableQuantityGreaterThan(int availableQuantity);

    List<Product> findByNameContainingIgnoreCase(String name, Sort sort);

    List<Product> findTop4ByCategoryAndIdNot(String category, Long id);
}
