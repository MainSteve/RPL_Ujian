package com.mycompany.rpl_ujian.service;

import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User register(User user) {
        // Hash password
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    // Helper to create initial admin if not exists
    @Transactional
    public void createAdminIfNotExists() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin123", "ADMIN", "Administrator");
            register(admin);
        }
    }
}
