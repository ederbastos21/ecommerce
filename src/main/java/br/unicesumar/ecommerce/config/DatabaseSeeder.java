package br.unicesumar.ecommerce.config;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                System.out.println("✅ Banco já possui produtos. Alimentador não será executado.");
                return;
            }

            System.out.println("⚙️ Iniciando alimentação de produtos...");

            List<Product> products = new ArrayList<>();
            Random random = new Random();

            String[] categories = {
                    "processors", "motherboards", "graphics-cards", "memory", "storage-ssd",
                    "storage-hdd", "cooling", "power-supplies", "cases", "monitors",
                    "keyboards", "mice", "headsets", "networking", "peripherals", "cables"
            };

            for (int i = 1; i <= 100; i++) {
                String category = categories[random.nextInt(categories.length)];
                String name = generateProductName(category, i);

                // 🔹 Faixa de preço realista por categoria
                BigDecimal price;
                switch (category) {
                    case "processors" -> price = BigDecimal.valueOf(600 + random.nextInt(2600)); // 600 a 3200
                    case "motherboards" -> price = BigDecimal.valueOf(400 + random.nextInt(1600)); // 400 a 2000
                    case "graphics-cards" -> price = BigDecimal.valueOf(1000 + random.nextInt(4000)); // 1000 a 5000
                    case "memory" -> price = BigDecimal.valueOf(150 + random.nextInt(600)); // 150 a 750
                    case "storage-ssd" -> price = BigDecimal.valueOf(200 + random.nextInt(800)); // 200 a 1000
                    case "storage-hdd" -> price = BigDecimal.valueOf(150 + random.nextInt(350)); // 150 a 500
                    case "cooling" -> price = BigDecimal.valueOf(100 + random.nextInt(400)); // 100 a 500
                    case "power-supplies" -> price = BigDecimal.valueOf(250 + random.nextInt(550)); // 250 a 800
                    case "cases" -> price = BigDecimal.valueOf(200 + random.nextInt(800)); // 200 a 1000
                    case "monitors" -> price = BigDecimal.valueOf(700 + random.nextInt(2300)); // 700 a 3000
                    case "keyboards" -> price = BigDecimal.valueOf(100 + random.nextInt(400)); // 100 a 500
                    case "mice" -> price = BigDecimal.valueOf(50 + random.nextInt(250)); // 50 a 300
                    case "headsets" -> price = BigDecimal.valueOf(150 + random.nextInt(350)); // 150 a 500
                    case "networking" -> price = BigDecimal.valueOf(100 + random.nextInt(400)); // 100 a 500
                    case "peripherals" -> price = BigDecimal.valueOf(50 + random.nextInt(200)); // 50 a 250
                    case "cables" -> price = BigDecimal.valueOf(20 + random.nextInt(80)); // 20 a 100
                    default -> price = BigDecimal.valueOf(100 + random.nextInt(900));
                }

                // 🔹 Desconto probabilístico (~66% dos produtos)
                int discount = 0;
                if (random.nextDouble() < 0.66) {
                    discount = 5 + random.nextInt(26); // 5 a 30%
                }

                BigDecimal shippingValue = BigDecimal.valueOf(20 + random.nextInt(80));
                String description = "Produto da categoria " + category +
                        " com excelente desempenho e qualidade. Item número " + i +
                        ". Ideal para quem busca performance e confiabilidade.";

                // 🔹 Quantidades realistas
                int availableQuantity = switch (category) {
                    case "processors", "graphics-cards", "motherboards" -> 5 + random.nextInt(20);
                    case "memory", "storage-ssd", "storage-hdd" -> 10 + random.nextInt(50);
                    default -> 20 + random.nextInt(80);
                };
                int ammountSold = random.nextInt(availableQuantity + 20);

                String imageFileName = chooseRandomImage(category, random);

                Product p = new Product();
                p.setName(name);
                p.setPrice(price);
                p.setDiscount(discount);
                p.setShippingValue(shippingValue);
                p.setDescription(description);
                p.setCategory(category);
                p.setAvailableQuantity(availableQuantity);
                p.setAmmountSold(ammountSold);
                p.setImageFileName(imageFileName);

                BigDecimal discountPrice = price.subtract(
                        price.multiply(BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100))
                );
                p.setDiscountPrice(discountPrice);

                products.add(p);
            }

            productRepository.saveAll(products);
            System.out.println("✅ Alimentação concluída com sucesso! " + products.size() + " produtos criados.");
        };
    }

    // 🔹 Método para gerar nomes de produtos por categoria
    private String generateProductName(String category, int index) {
        return switch (category) {
            case "processors" -> "Processador Ryzen " + (index + 1000);
            case "motherboards" -> "Placa-Mãe ASUS Prime B" + (index + 400);
            case "graphics-cards" -> "Placa de Vídeo RTX " + (index + 2000);
            case "memory" -> "Memória DDR4 HyperX " + (index * 2) + "GB";
            case "storage-ssd" -> "SSD Kingston " + (index * 2) + "GB NVMe";
            case "storage-hdd" -> "HD Seagate " + (index * 100) + "GB";
            case "cooling" -> "Cooler Master Hyper " + (index + 100);
            case "power-supplies" -> "Fonte Corsair " + (400 + index) + "W 80 Plus Bronze";
            case "cases" -> "Gabinete Gamer NZXT Modelo " + index;
            case "monitors" -> "Monitor LG UltraWide " + (20 + index % 10) + "''";
            case "keyboards" -> "Teclado Mecânico Redragon " + index;
            case "mice" -> "Mouse Logitech G" + (100 + index);
            case "headsets" -> "Headset HyperX Cloud " + index;
            case "networking" -> "Roteador TP-Link Archer " + index;
            case "peripherals" -> "Controle USB Genérico " + index;
            case "cables" -> "Cabo HDMI 2.1 " + index + "m";
            default -> "Produto Genérico " + index;
        };
    }

    // 🔹 Método para escolher imagens aleatórias
    private String chooseRandomImage(String category, Random random) {
        int maxImagesPerCategory = 5; // Ajuste se tiver mais imagens
        int imageNumber = random.nextInt(maxImagesPerCategory) + 1;
        return category + "-" + imageNumber + ".jpg";
    }
}
