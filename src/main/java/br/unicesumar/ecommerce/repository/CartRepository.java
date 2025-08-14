package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Cart;
import br.unicesumar.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndFinalizedFalse(User user);
}