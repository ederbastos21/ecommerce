package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Product;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.service.FileStorageService;
import br.unicesumar.ecommerce.service.ProductService;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException; // IMPORTADO
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // IMPORTADO

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    // CONSTRUTOR ATUALIZADO
    public AdminController(
        UserService userService,
        ProductService productService,
        FileStorageService fileStorageService
    ) {
        this.userService = userService;
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    //admin home loader
    @GetMapping
    public String adminHome(HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeTable", "users");
        return "admin";
    }

    //======================== USERS =========================
    // (Seção de Users permanece inalterada)

    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeTable", "users");
        return "admin";
    }

    // MÉTODO 'deleteUser' ATUALIZADO COM TRATAMENTO DE ERRO (Recomendado)
    @PostMapping("/users/delete/{id}")
    public String deleteUser(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Usuário deletado com sucesso!"
            );
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Erro: Este usuário não pode ser deletado pois está associado a pedidos."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Erro inesperado ao deletar usuário."
            );
        }
        return "redirect:/admin/users";
    }

    //======================== PRODUCTS =========================

    @GetMapping("/products")
    public String listProducts(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("products", productService.getAll());
        model.addAttribute("activeTable", "products");
        return "admin";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("product", new Product());
        model.addAttribute("activeTable", "products");
        return "productForm";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(
        @PathVariable Long id,
        Model model,
        HttpSession session
    ) {
        try {
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
                return "redirect:/";
            }
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("activeTable", "products");
            return "productForm";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }

    /**
     * MÉTODO 'saveProduct' (Correto, como estava antes)
     */
    @PostMapping("/products/save")
    public String saveProduct(
        @ModelAttribute Product product,
        @RequestParam("imageFile") MultipartFile imageFile,
        HttpSession session
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        BigDecimal desconto = new BigDecimal(product.getDiscount());
        desconto = desconto.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal precoFinal = product.getPrice().subtract(product.getPrice().multiply(desconto));
        product.setDiscountPrice(precoFinal);

        System.out.println("Preço final com desconto: " + product.getDiscountPrice());


        // Lógica de Upload de Imagem
        if (imageFile != null && !imageFile.isEmpty()) {
            // 1. Salva o novo arquivo e obtém o nome único
            String fileName = fileStorageService.storeFile(imageFile);
            // 2. Define o nome do arquivo no objeto produto
            product.setImageFileName(fileName);
        } else if (product.getId() != null) {
            // 3. Caso de EDIÇÃO
            try {
                Product oldProduct = productService.findById(product.getId());
                product.setImageFileName(oldProduct.getImageFileName());
            } catch (Exception e) {
                // Produto não encontrado
            }
        }

        productService.save(product);
        return "redirect:/admin/products";
    }

    /**
     * MÉTODO 'deleteProduct' ATUALIZADO COM TRATAMENTO DE ERRO
     */
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes // Adicionado
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/";
        }

        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Produto deletado com sucesso!"
            );
        } catch (DataIntegrityViolationException e) {
            // Captura o erro de chave estrangeira
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Erro: Este produto não pode ser deletado pois já está associado a um pedido."
            );
        } catch (Exception e) {
            // Captura outros erros inesperados
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Ocorreu um erro inesperado ao tentar deletar o produto."
            );
        }

        return "redirect:/admin/products";
    }
}
