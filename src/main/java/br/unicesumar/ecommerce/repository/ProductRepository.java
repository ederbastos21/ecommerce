package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

// Note: Os imports 'User' e 'BigDecimal' não estavam sendo usados aqui,
// então eu os removi para limpar o código.

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    Optional<Product> findById(Long id);
    List<Product> findTop12ByOrderByAmmountSoldDesc();
    List<Product> findByAvailableQuantityGreaterThan(int availableQuantity);

    // =============================================
    // INÍCIO DOS MÉTODOS FALTANTES QUE CAUSARAM O ERRO
    // =============================================

    /**
     * Método necessário para a barra de busca "search" funcionar
     * em conjunto com a ordenação.
     */
    List<Product> findByNameContainingIgnoreCase(String name, Sort sort);

    /**
     * Método otimizado para buscar os 4 produtos relacionados
     * (da mesma categoria, excluindo o ID do produto atual).
     */
    List<Product> findTop4ByCategoryAndIdNot(String category, Long id);

    // =============================================
    // FIM DOS MÉTODOS FALTANTES
    // =============================================
}
