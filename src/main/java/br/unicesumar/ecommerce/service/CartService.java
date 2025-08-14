package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.*;
import br.unicesumar.ecommerce.repository.CartRepository;
import br.unicesumar.ecommerce.repository.ProductRepository;
import br.unicesumar.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getActiveCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return cartRepository.findByUserAndFinalizedFalse(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = getActiveCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        cart.addItem(product, quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public void finalizeCart(Long userId) {
        Cart cart = getActiveCart(userId);
        cart.setFinalized(true);
        cartRepository.save(cart);
    }
}