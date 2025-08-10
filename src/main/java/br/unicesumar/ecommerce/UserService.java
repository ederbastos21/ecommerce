package br.unicesumar.ecommerce;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User processUser(User user){
        user.setName(user.getName());
        user.setAge(user.getAge());
        user.setCpf(user.getCpf());
        user.setEmail(user.getEmail());
        user.setAddress(user.getAddress());
        return user;
    }
}
