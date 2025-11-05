package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}

