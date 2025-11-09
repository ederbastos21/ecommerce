package br.unicesumar.ecommerce.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FreightController {

    @GetMapping("/calculate")
    public double calculate(@RequestParam String state) {

        double baseValue = 20;
        double valuePerKm = 0.02;

        switch (state.toUpperCase()) { // aceita siglas em maiúsculo
            case "AC": // Acre
                return baseValue + (3319 * valuePerKm);
            case "AL": // Alagoas
                return baseValue + (2766 * valuePerKm);
            case "AP": // Amapá
                return baseValue + (3526 * valuePerKm);
            case "AM": // Amazonas
                return baseValue + (6539 * valuePerKm);
            case "BA": // Bahia
                return baseValue + (2272 * valuePerKm);
            case "CE": // Ceará
                return baseValue + (3141 * valuePerKm);
            case "DF": // Distrito Federal
                return baseValue + (1275 * valuePerKm);
            case "ES": // Espírito Santo
                return baseValue + (1395 * valuePerKm);
            case "GO": // Goiás
                return baseValue + (1086 * valuePerKm);
            case "MA": // Maranhão
                return baseValue + (3076 * valuePerKm);
            case "MT": // Mato Grosso
                return baseValue + (1519 * valuePerKm);
            case "MS": // Mato Grosso do Sul
                return baseValue + (833 * valuePerKm);
            case "MG": // Minas Gerais
                return baseValue + (1059 * valuePerKm);
            case "PA": // Pará
                return baseValue + (509 * valuePerKm);
            case "PB": // Paraíba
                return baseValue + (3076 * valuePerKm);
            case "PR": // Paraná
                return baseValue + (123 * valuePerKm);
            case "PE": // Pernambuco
                return baseValue + (2995 * valuePerKm);
            case "PI": // Piauí
                return baseValue + (2845 * valuePerKm);
            case "RJ": // Rio de Janeiro
                return baseValue + (969 * valuePerKm);
            case "RN": // Rio Grande do Norte
                return baseValue + (3186 * valuePerKm);
            case "RS": // Rio Grande do Sul
                return baseValue + (707 * valuePerKm);
            case "RO": // Rondônia
                return baseValue + (2922 * valuePerKm);
            case "RR": // Roraima
                return baseValue + (6041 * valuePerKm);
            case "SC": // Santa Catarina
                return baseValue + (405 * valuePerKm);
            case "SP": // São Paulo
                return baseValue + (504 * valuePerKm);
            case "SE": // Sergipe
                return baseValue + (2527 * valuePerKm);
            case "TO": // Tocantins
                return baseValue + (325 * valuePerKm);
            default:
                throw new IllegalArgumentException("Estado inválido: " + state);
        }
    }
}
