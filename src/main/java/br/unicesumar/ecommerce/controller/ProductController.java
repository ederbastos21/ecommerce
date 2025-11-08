package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.Purchase;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.PurchaseService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Iterator;

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
    public String index(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String sortParameter,
        Model model
    ) {
        model.addAttribute("sortParameter", sortParameter);
        if (sortParameter == null) {
            sortParameter = "relevance";
        }
        if (sortParameter.equals("priceAsc")) {
            Sort sort = Sort.by("price").ascending();
            model.addAttribute(
                "products",
                productService.findByNameContainingIgnoreCase(search, sort)
            );
        } else if (sortParameter.equals("priceDesc")) {
            Sort sort = Sort.by("price").descending();
            model.addAttribute(
                "products",
                productService.findByNameContainingIgnoreCase(search, sort)
            );
        } else if (sortParameter.equals("relevance")) {
            Sort sort = Sort.by("ammountSold").descending();
            model.addAttribute(
                "products",
                productService.findByNameContainingIgnoreCase(search, sort)
            );
        } else {
            Sort sort = Sort.unsorted();
            model.addAttribute(
                "products",
                productService.findByNameContainingIgnoreCase(search, sort)
            );
        }
        model.addAttribute("search", search);
        return "products";
    }

    //shows product detail individual page
    @GetMapping("/productDetail/{id}")
    public String ShowProductDetail(Model model,
                                    @PathVariable Long id,
                                    @ModelAttribute("successMessage") String successMessage)  {
        System.out.println(">>> Entrou em ShowProductDetail");
        Product product = productService.findById(id);
        model.addAttribute("product",  product);

        // produtos relacionados
        List<Product> relatedProducts = productService.findRelatedProducts(
            product.getCategory(),
            product.getId()
        );
        model.addAttribute("relatedProducts", relatedProducts);

        return "productDetail";
    }

    //=============== CART ===============

    //shows cart page
    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        List<Map<String, Object>> cart = getCart(session);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartItemView> cartItems = new ArrayList<>();

        for (Map<String, Object> item : cart) {
            Long id = (Long) item.get("productId");
            int quantity = (int) item.get("quantity");

            Product product = productService.findById(id);
            if (product != null) {
                CartItemView view = new CartItemView();
                view.setProduct(product);
                view.setQuantity(quantity);
                view.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

                totalPrice = totalPrice.add(view.getSubtotal());
                cartItems.add(view);
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartItemCount", getCartCount(cart));
        return "cart";
    }

    @PostMapping("/addToCart/{id}")
    public String addToCart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }

        List<Map<String, Object>> cart = getCart(session);

        boolean found = false;
        for (Map<String, Object> item : cart) {
            Long itemId = (Long) item.get("productId");
            if (itemId.equals(id)) {
                int oldQty = (int) item.get("quantity");
                item.put("quantity", oldQty + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("productId", id);
            newItem.put("name", product.getName());
            newItem.put("price", product.getPrice());
            newItem.put("quantity", quantity);
            cart.add(newItem);
        }

        updateCartSession(session, cart);
        redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado ao carrinho!");
        return "redirect:/productDetail/{id}";
    }

    @PostMapping("/updateCart/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam int quantity, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);

        Iterator<Map<String, Object>> iterator = cart.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> item = iterator.next();
            Long itemId = (Long) item.get("productId");

            if (itemId.equals(id)) {
                if (quantity <= 0) {
                    iterator.remove();
                } else {
                    item.put("quantity", quantity);
                }
                break;
            }
        }

        updateCartSession(session, cart);
        return "redirect:/cart";
    }

    @PostMapping("/removeFromCart/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        cart.removeIf(item -> item.get("productId").equals(id));
        updateCartSession(session, cart);
        return "redirect:/cart";
    }

    // ======================== MÉTODOS AUXILIARES ========================

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj == null) {
            List<Map<String, Object>> newCart = new ArrayList<>();
            session.setAttribute("cart", newCart);
            return newCart;
        }
        return (List<Map<String, Object>>) cartObj;
    }

    private int getCartCount(List<Map<String, Object>> cart) {
        return cart.stream().mapToInt(item -> (int) item.get("quantity")).sum();
    }

    private void updateCartSession(HttpSession session, List<Map<String, Object>> cart) {
        session.setAttribute("cart", cart);
        session.setAttribute("cartItemCount", getCartCount(cart));
    }


    //processes purchase
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/buy")
    public String processPurchase(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Carrinho vazio!");
            return "redirect:/cart";
        }

        try {
            Purchase purchase = purchaseService.createPurchase(loggedUser, cart);

            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                productService.updateStock(entry.getKey(), entry.getValue());
            }

            session.removeAttribute("cart");

            redirectAttributes.addFlashAttribute("successMessage", "Compra realizada com sucesso! Pedido #" + purchase.getId());
            return "redirect:/purchaseHistory";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar compra: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    //inner class for cart display
    public static class CartItemView {

        private Product product;
        private Integer quantity;
        private BigDecimal subtotal;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}
