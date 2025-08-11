package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
