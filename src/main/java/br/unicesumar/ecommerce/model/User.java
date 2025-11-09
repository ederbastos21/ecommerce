package br.unicesumar.ecommerce.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // wrapper

    private String name;
    private int age;
    private String cpf;

    @Column(unique = true)
    private String email;

    private String address;
    private String password;
    private String role;
    private int token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Address> addresses;

    private Long favoriteAddressId;

    private Long favoritePaymentMethodId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PaymentMethod> paymentMethods;


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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public void addPurchase(Purchase purchase) {
        purchases.add(purchase);
        purchase.setUser(this);
    }

    public void removePurchase(Purchase purchase) {
        purchases.remove(purchase);
        purchase.setUser(null);
    }

    public java.util.List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(java.util.List<Address> addresses) {
        this.addresses = addresses;
    }

    public java.util.List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(java.util.List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Long getFavoriteAddressId() {
        return favoriteAddressId;
    }

    public void setFavoriteAddressId(Long favoriteAddressId) {
        this.favoriteAddressId = favoriteAddressId;
    }

    public Long getFavoritePaymentMethodId() {
        return favoritePaymentMethodId;
    }

    public void setFavoritePaymentMethodId(Long favoritePaymentMethodId) {
        this.favoritePaymentMethodId = favoritePaymentMethodId;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
