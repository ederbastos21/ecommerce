package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Purchase;
import br.unicesumar.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserOrderByPurchaseDateDesc(User user);
    List<Purchase> findByUserIdOrderByPurchaseDateDesc(Long userId);
}
