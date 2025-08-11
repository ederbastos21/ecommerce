package br.unicesumar.ecommerce.repository;

import br.unicesumar.ecommerce.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Busca categorias raiz com filhos carregados para evitar N+1
    @EntityGraph(attributePaths = {"children", "children.children"})
    List<Category> findByParentIsNull();
}


