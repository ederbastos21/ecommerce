package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.*;
import br.unicesumar.ecommerce.repository.AddressRepository;
import br.unicesumar.ecommerce.repository.PaymentMethodRepository;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentRepository;
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public static class CartItemView {

        private Product product;
        private int quantity;

        public CartItemView(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getSubtotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    public CheckoutController(
        AddressRepository addressRepository,
        PaymentMethodRepository paymentRepository,
        ProductService productService,
        PurchaseService purchaseService
    ) {
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cart = (List<
            Map<String, Object>
        >) session.getAttribute("cart");

        if (user == null) {
            return "redirect:/login";
        }
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        Address favoriteAddress = null;
        if (user.getFavoriteAddressId() != null) {
            favoriteAddress = addressRepository
                .findById(user.getFavoriteAddressId())
                .orElse(null);
        }

        PaymentMethod favoritePayment = null;
        if (user.getFavoritePaymentMethodId() != null) {
            favoritePayment = paymentRepository
                .findById(user.getFavoritePaymentMethodId())
                .orElse(null);
        }

        List<CartItemView> cartItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map<String, Object> item : cart) {
            Long productId = (Long) item.get("productId");
            Integer quantity = (Integer) item.get("quantity");

            Product product = productService.findById(productId);
            if (product != null) {
                CartItemView itemView = new CartItemView(product, quantity);
                cartItems.add(itemView);
                totalPrice = totalPrice.add(itemView.getSubtotal());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("favoriteAddress", favoriteAddress);
        model.addAttribute("favoritePayment", favoritePayment);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout";
    }

    @PostMapping("/complete")
    public String completePurchase(
        @RequestParam("installments") int installments,
        HttpSession session,
        Model model
    ) {
        User user = (User) session.getAttribute("loggedUser");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartList = (List<
            Map<String, Object>
        >) session.getAttribute("cart");

        if (user == null) {
            return "redirect:/login";
        }
        if (cartList == null || cartList.isEmpty()) {
            return "redirect:/cart";
        }

        Map<Long, Integer> cartMap = new HashMap<>();
        for (Map<String, Object> item : cartList) {
            Long productId = (Long) item.get("productId");
            Integer quantity = (Integer) item.get("quantity");
            cartMap.put(productId, quantity);
        }

        try {
            purchaseService.createPurchase(user, cartMap, installments);

            session.removeAttribute("cart");
            session.removeAttribute("cartItemCount");

            return "redirect:/purchaseHistory";
        } catch (IllegalArgumentException e) {
            model.addAttribute("checkoutError", e.getMessage());
            return showCheckoutPage(session, model);
        } catch (Exception e) {
            model.addAttribute(
                "checkoutError",
                "Ocorreu um erro ao processar sua compra. Tente novamente."
            );
            return showCheckoutPage(session, model);
        }
    }
}
