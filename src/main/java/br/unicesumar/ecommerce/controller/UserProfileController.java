package br.unicesumar.ecommerce.controller;

import br.unicesumar.ecommerce.model.Address;
import br.unicesumar.ecommerce.model.PaymentMethod;
import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.AddressRepository;
import br.unicesumar.ecommerce.repository.PaymentMethodRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/userProfile")
public class UserProfileController {

    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentRepository;

    public UserProfileController(AddressRepository addressRepository, PaymentMethodRepository paymentRepository) {
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("addresses", addressRepository.findByUserId(user.getId()));
        model.addAttribute("payments", paymentRepository.findByUserId(user.getId()));
        model.addAttribute("newAddress", new Address());
        model.addAttribute("newPayment", new PaymentMethod());
        return "userProfile";
    }

    @PostMapping("/addAddress")
    public String addAddress(@ModelAttribute Address address, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            address.setUser(user);
            addressRepository.save(address);
        }
        return "redirect:/userProfile";
    }

    @PostMapping("/addPayment")
    public String addPayment(@ModelAttribute PaymentMethod payment, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            payment.setUser(user);
            paymentRepository.save(payment);
        }
        return "redirect:/userProfile";
    }

    @PostMapping("/deleteAddress/{id}")
    public String deleteAddress(@PathVariable Long id) {
        addressRepository.deleteById(id);
        return "redirect:/userProfile";
    }

    @PostMapping("/deletePayment/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentRepository.deleteById(id);
        return "redirect:/userProfile";
    }
}

