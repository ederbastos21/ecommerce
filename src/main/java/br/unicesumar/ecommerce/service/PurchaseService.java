package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.*;
import br.unicesumar.ecommerce.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public Purchase createPurchase(User user, Map<Long, Integer> cart, int installments, BigDecimal freightCost, Address shippingAddress, PaymentMethod paymentMethod) {
        
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus("COMPLETED");
        purchase.setInstallments(installments);
        purchase.setFreightAmount(freightCost);
        purchase.setShippingAddress(shippingAddress);
        purchase.setPaymentMethod(paymentMethod);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productService.findById(productId);
            if (product != null && quantity > 0) {
                
                if (product.getAvailableQuantity() < quantity) {
                    throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: " + product.getName()
                    );
                }
                productService.updateStock(productId, quantity);

                PurchaseItem item = new PurchaseItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setPriceAtPurchase(product.getPrice());
                purchase.addItem(item);

                subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BigDecimal totalBeforeInterest = subtotal.add(freightCost);

        BigDecimal finalTotal = totalBeforeInterest;

        if (installments > 1) {
            BigDecimal monthlyInterest = new BigDecimal("0.02");
            BigDecimal totalInterest = monthlyInterest.multiply(BigDecimal.valueOf(installments));
            finalTotal = totalBeforeInterest.multiply(BigDecimal.ONE.add(totalInterest))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        purchase.setTotalAmount(finalTotal); 
        
        Purchase savedPurchase = purchaseRepository.save(purchase);
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