package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //home loader
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("role", loggedUser.getRole());
        }

        model.addAttribute("products", productService.getFirst12Products());
        return "index";
    }

    //=============== PRODUCTS ===============

    //shows products in products page using sort
    @GetMapping("/products")
    public String index(@RequestParam (required = false) String search, @RequestParam (required = false) String sortParameter, Model model) {
        model.addAttribute("sortParameter",sortParameter);
        if (sortParameter == null){
            sortParameter = "relevance";
        }
        if (sortParameter.equals("priceAsc")) {
            Sort sort = Sort.by("price").ascending();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        } else if (sortParameter.equals("priceDesc")) {
            Sort sort = Sort.by("price").descending();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        } else if (sortParameter.equals("relevance")) {
            Sort sort = Sort.by("ammountSold").descending();
            model.addAttribute("products",productService.findByNameContainingIgnoreCase(search, sort));
        } else {
            Sort sort = Sort.unsorted();
            model.addAttribute("products", productService.findByNameContainingIgnoreCase(search, sort));
        }
        model.addAttribute("search",search);
        return "products";
    }

    //shows product detail individual page
    @GetMapping("/productDetail/{id}")
    public String ShowProductDetail(Model model, @PathVariable Long id){
        Product product = productService.findById(id);
        model.addAttribute("product",product);
        return "productDetail";
    }

    //=============== CART ===============

    //shows cart page
    @GetMapping("/cart")
    public String cart(HttpSession session, Model model){
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("totalPrice", BigDecimal.ZERO);
            return "cart";
        }

        List<CartItemView> cartItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null) {
                CartItemView item = new CartItemView();
                item.setProduct(product);
                item.setQuantity(entry.getValue());
                item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));

                cartItems.add(item);
                totalPrice = totalPrice.add(item.getSubtotal());
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    //adds item to cart
    @PostMapping("/addToCart/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session, Model model){
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            model.addAttribute("userNotLoggedError", "Favor logar para acessar o carrinho");
            return "/login";
        }

        if (quantity <= 0) {
            quantity = 1;
        }

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null){
            cart = new HashMap<>();
        }

        cart.merge(id, quantity, Integer::sum);
        session.setAttribute("cart", cart);

        return "redirect:/productDetail/{id}";
    }

    //removes item from cart
    @PostMapping("/removeFromCart/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(id);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    //updates item quantity in cart
    @PostMapping("/updateCart/{id}")
    public String updateCart(@PathVariable Long id,
                             @RequestParam int quantity,
                             HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            if (quantity <= 0) {
                cart.remove(id);
            } else {
                cart.put(id, quantity);
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    //processes purchase
    @PostMapping("/buy")
    public String processPurchase(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            // update stock products
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                productService.updateStock(entry.getKey(), entry.getValue());
            }

            // clean cart
            session.removeAttribute("cart");

            model.addAttribute("successMessage", "Compra realizada com sucesso!");
            return "purchaseSuccess";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    //inner class for cart display
    public static class CartItemView {
        private Product product;
        private Integer quantity;
        private BigDecimal subtotal;

        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}