package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Address;
import br.unicesumar.ecommerce.model.PaymentMethod;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.AddressRepository;
import br.unicesumar.ecommerce.repository.PaymentMethodRepository;
import br.unicesumar.ecommerce.repository.UserRepository;
import br.unicesumar.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/userProfile")
public class UserProfileController {

    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentRepository;
    private final UserRepository userRepository;

    public UserProfileController(AddressRepository addressRepository, PaymentMethodRepository paymentRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // Página principal do perfil
    @GetMapping
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "userProfile";
    }

    // Página de endereços
    @GetMapping("/addresses")
    public String addresses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("addresses", addressRepository.findByUserId(user.getId()));
        model.addAttribute("newAddress", new Address());
        return "addresses";
    }

    // Página de métodos de pagamento
    @GetMapping("/payments")
    public String payments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("payments", paymentRepository.findByUserId(user.getId()));
        model.addAttribute("newPayment", new PaymentMethod());
        return "payments";
    }

    @PostMapping("/addAddress")
    public String addAddress(@ModelAttribute Address address, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            address.setUser(user);
            addressRepository.save(address); // id é gerado aqui

            if (user.getFavoriteAddressId() == null) {
                user.setFavoriteAddressId(address.getId());
                userRepository.save(user);
            }
        }
        return "redirect:/userProfile/addresses";
    }

    @PostMapping("/addPayment")
    public String addPayment(@ModelAttribute PaymentMethod payment, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            payment.setUser(user);
            paymentRepository.save(payment);

            if (user.getFavoritePaymentMethodId() == null) {
                user.setFavoritePaymentMethodId(payment.getId());
                userRepository.save(user);
            }
        }
        return "redirect:/userProfile/payments";
    }


    @PostMapping("/deleteAddress/{id}")
    public String deleteAddress(@PathVariable Long id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        Address address = addressRepository.findById(id).orElse(null);

        if (address.getId().equals(loggedUser.getFavoriteAddressId())){
            loggedUser.setFavoriteAddressId(null);
            userRepository.save(loggedUser);
        }

        addressRepository.deleteById(id);
        return "redirect:/userProfile/addresses";
    }

    @PostMapping("/deletePayment/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentRepository.deleteById(id);
        return "redirect:/userProfile/payments";
    }

    @PostMapping("/changeFavoriteAddress/{id}")
    public String changeFavoriteAddress(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            user.setFavoriteAddressId(id);
            userRepository.save(user);
        }
        return "redirect:/userProfile/addresses";
    }

    @PostMapping("/changeFavoritePayment/{id}")
    public String changeFavoritePayment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            user.setFavoritePaymentMethodId(id);
            userRepository.save(user);
            session.setAttribute("loggedUser", user); // Atualiza a sessão
        }
        return "redirect:/userProfile/payments";
    }

}

