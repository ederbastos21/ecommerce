package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Cart;
import br.unicesumar.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add/{productId}")
    public Cart addProduct(@PathVariable Long userId, @PathVariable Long productId, @RequestParam int quantity) {
        return cartService.addProductToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getActiveCart(userId);
    }

    @PostMapping("/{userId}/finalize")
    public void finalizeCart(@PathVariable Long userId) {
        cartService.finalizeCart(userId);
    }
}