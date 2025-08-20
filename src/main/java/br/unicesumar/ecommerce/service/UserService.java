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
        return userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
