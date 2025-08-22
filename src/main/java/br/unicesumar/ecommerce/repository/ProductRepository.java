package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name, Sort sort);
}
