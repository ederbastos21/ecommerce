package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
