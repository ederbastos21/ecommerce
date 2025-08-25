package br.unicesumar.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private int discount;
    private BigDecimal shippingValue;
    private String description;
    private String category;
    private int availableQuantity;
    private int ammountSold;

    public Product() {
    }

    public Product(Long id, String name, BigDecimal price, int discount, BigDecimal shippingValue, String description, String category, int availableQuantity, int ammountSold) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.shippingValue = shippingValue;
        this.description = description;
        this.category = category;
        this.availableQuantity = availableQuantity;
        this.ammountSold = ammountSold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public BigDecimal getShippingValue() {
        return shippingValue;
    }

    public void setShippingValue(BigDecimal shippingValue) {
        this.shippingValue = shippingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getAmmountSold(){
        return ammountSold;
    }

    public void setAmmountSold(int ammountSold){
        this.ammountSold = ammountSold;
    }
}
