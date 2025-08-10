package br.unicesumar.ecommerce;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        user.setName(user.getName());
        user.setAge(user.getAge());
        user.setCpf(user.getCpf());
        user.setEmail(user.getEmail());
        user.setAddress(user.getAddress());
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }
}
