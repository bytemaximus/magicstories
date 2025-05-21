package com.bytemaximus.magicstories.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPassword("password");

        userRepository.save(user);
    }



    public List<User> getUserByEmail(String email) {
        return Collections.singletonList(userRepository.getUserByEmail(email));
    }
}
