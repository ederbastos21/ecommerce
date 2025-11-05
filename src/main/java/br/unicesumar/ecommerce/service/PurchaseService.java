package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.Purchase;
import br.unicesumar.ecommerce.model.PurchaseItem;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public Purchase createPurchase(User user, Map<Long, Integer> cart) {
        System.out.println("criando compra para: " + user.getId() + "");
        System.out.println("tamanho do carrinho: " + cart.size());
        
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus("compra realizada");

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productService.findById(productId);
            if (product != null && quantity > 0) {
                PurchaseItem item = new PurchaseItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setPriceAtPurchase(product.getPrice());
                purchase.addItem(item);

                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
        }

        purchase.setTotalAmount(total);
        Purchase savedPurchase = purchaseRepository.save(purchase);
        System.out.println("Purchase saved with ID: " + savedPurchase.getId());
        System.out.println("Number of items: " + savedPurchase.getItems().size());
        return savedPurchase;
    }

    public List<Purchase> getUserPurchases(User user) {
        return purchaseRepository.findByUserOrderByPurchaseDateDesc(user);
    }

    public List<Purchase> getUserPurchasesById(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchaseDateDesc(userId);
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }
}
