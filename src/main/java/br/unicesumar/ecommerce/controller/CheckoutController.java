package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.*;
import br.unicesumar.ecommerce.repository.AddressRepository;
import br.unicesumar.ecommerce.repository.PaymentMethodRepository;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentRepository;
    private final ProductService productService;
    private final PurchaseService purchaseService;
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("250");

    public static class CartItemView {
        private Product product;
        private int quantity;
        public CartItemView(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public BigDecimal getSubtotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    public CheckoutController(AddressRepository addressRepository,
                              PaymentMethodRepository paymentRepository,
                              ProductService productService,
                              PurchaseService purchaseService) {
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    private BigDecimal calculateFreight(User user) {
        if (user != null && user.getFavoriteAddressId() != null) {
            Long favId = user.getFavoriteAddressId();
            Address favoriteAddress = addressRepository.findById(favId).orElse(null);

            if (favoriteAddress != null) {
                String state = favoriteAddress.getState();
                String url = String.format("http://localhost:8080/api/calculate?state=%s", state);
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    Double freight = restTemplate.getForObject(url, Double.class);
                    return BigDecimal.valueOf(freight != null ? freight : 0.0);
                } catch (Exception e) {
                    System.out.println("Erro ao calcular frete: " + e.getMessage());
                    return BigDecimal.ZERO;
                }
            }
        }
        return null;
    }

    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");

        if (user == null) {
            return "redirect:/login";
        }
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        Address favoriteAddress = null;
        if (user.getFavoriteAddressId() != null) {
            favoriteAddress = addressRepository.findById(user.getFavoriteAddressId()).orElse(null);
        }

        PaymentMethod favoritePayment = null;
        if (user.getFavoritePaymentMethodId() != null) {
            favoritePayment = paymentRepository.findById(user.getFavoritePaymentMethodId()).orElse(null);
        }

        List<CartItemView> cartItems = new ArrayList<>();
        BigDecimal subtotalPrice = BigDecimal.ZERO;

        for (Map<String, Object> item : cart) {
            Long productId = (Long) item.get("productId");
            Integer quantity = (Integer) item.get("quantity");

            Product product = productService.findById(productId);
            if (product != null) {
                CartItemView itemView = new CartItemView(product, quantity);
                cartItems.add(itemView);
                subtotalPrice = subtotalPrice.add(itemView.getSubtotal());
            }
        }
        
        BigDecimal finalFreightCost = calculateFreight(user);

        if (finalFreightCost != null && subtotalPrice.compareTo(FREE_SHIPPING_THRESHOLD) > 0) {
            finalFreightCost = BigDecimal.ZERO;
        }

        BigDecimal finalTotal = subtotalPrice;
        if (finalFreightCost != null) {
            finalTotal = subtotalPrice.add(finalFreightCost);
        }

        model.addAttribute("user", user);
        model.addAttribute("favoriteAddress", favoriteAddress);
        model.addAttribute("favoritePayment", favoritePayment);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotalPrice", subtotalPrice);
        model.addAttribute("freightCost", finalFreightCost);
        model.addAttribute("finalTotal", finalTotal);

        return "checkout";
    }

    @PostMapping("/complete")
    public String completePurchase(@RequestParam("installments") int installments,
                                   HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) session.getAttribute("cart");

        if (user == null) {
            return "redirect:/login";
        }
        if (cartList == null || cartList.isEmpty()) {
            return "redirect:/cart";
        }

        Address shippingAddress = addressRepository.findById(user.getFavoriteAddressId()).orElse(null);
        PaymentMethod paymentMethod = paymentRepository.findById(user.getFavoritePaymentMethodId()).orElse(null);

        if (shippingAddress == null || paymentMethod == null) {
            model.addAttribute("checkoutError", "Endereço ou pagamento favorito não encontrado.");
            return showCheckoutPage(session, model);
        }

        Map<Long, Integer> cartMap = new HashMap<>();
        BigDecimal subtotalPrice = BigDecimal.ZERO;

        for (Map<String, Object> item : cartList) {
            Long productId = (Long) item.get("productId");
            Integer quantity = (Integer) item.get("quantity");
            cartMap.put(productId, quantity);

            Product product = productService.findById(productId);
            if (product != null) {
                subtotalPrice = subtotalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BigDecimal finalFreightCost = calculateFreight(user);

        if (finalFreightCost != null && subtotalPrice.compareTo(FREE_SHIPPING_THRESHOLD) > 0) {
            finalFreightCost = BigDecimal.ZERO;
        }

        if (finalFreightCost == null) {
             model.addAttribute("checkoutError", "Não foi possível calcular o frete. Verifique seu endereço.");
            return showCheckoutPage(session, model);
        }

        try {
            purchaseService.createPurchase(user, cartMap, installments, finalFreightCost, shippingAddress, paymentMethod);

            session.removeAttribute("cart");
            session.removeAttribute("cartItemCount");

            return "redirect:/purchaseHistory";

        } catch (IllegalArgumentException e) {
            model.addAttribute("checkoutError", e.getMessage());
            return showCheckoutPage(session, model);
        } catch (Exception e) {
            model.addAttribute("checkoutError", "Ocorreu um erro ao processar sua compra. Tente novamente.");
            return showCheckoutPage(session, model);
        }
    }
}