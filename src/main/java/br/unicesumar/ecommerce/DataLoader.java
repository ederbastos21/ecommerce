package br.unicesumar.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Product> products = List.of(
                new Product("Laptop", new BigDecimal("1500.00")),
                new Product("Smartphone", new BigDecimal("800.00")),
                new Product("Headphones", new BigDecimal("150.00")),
                new Product("Keyboard", new BigDecimal("100.00")),
                new Product("Mouse", new BigDecimal("50.00")),
                new Product("Monitor", new BigDecimal("300.00")),
                new Product("Printer", new BigDecimal("200.00")),
                new Product("Camera", new BigDecimal("450.00")),
                new Product("Smartwatch", new BigDecimal("250.00")),
                new Product("Tablet", new BigDecimal("400.00"))
        );

        productRepository.saveAll(products);
    }
}
