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

    /**
     * BUG 1 (CORRIGIDO):
     * A implementação anterior estava errada. Ela ignorava a busca (name) e a
     * ordenação (sort).
     * Esta nova implementação verifica se a string de busca é nula ou vazia.
     * - Se for, retorna todos os produtos (respeitando a ordenação).
     * - Se não for, chama o método correto do repositório para filtrar por nome.
     */
    public List<Product> findByNameContainingIgnoreCase(
        String name,
        Sort sort
    ) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll(sort);
        }
        return productRepository.findByNameContainingIgnoreCase(name, sort);
    }

    /**
     * BUG 2 (CORRIGIDO):
     * A implementação anterior chamava 'findByCategoryIgnoreCase',
     * que não existe no repositório. Corrigido para 'findByCategory'.
     */
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category); // Corrigido
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

    /**
     * BUG 3 (OTIMIZADO):
     * A implementação anterior buscava TODOS os produtos da categoria no banco
     * para depois filtrar em Java (usando stream().filter()).
     * Esta nova versão chama o método 'findTop4ByCategoryAndIdNot' do repositório,
     * que já faz o filtro e a limitação direto no banco de dados, sendo
     * muito mais eficiente.
     */
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
