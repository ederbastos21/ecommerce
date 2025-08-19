package br.unicesumar.ecommerce.service;

import br.unicesumar.ecommerce.model.User;
import br.unicesumar.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        user.setName(user.getName());
        user.setAge(user.getAge());
        user.setCpf(user.getCpf());
        user.setEmail(user.getEmail());
        user.setAddress(user.getAddress());
        user.setPassword(user.getPassword());
        user.setRole(user.getRole());
        return userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
