package com.example.oauth.service;

import com.example.oauth.model.User;
import com.example.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public User save(User user){
       return userRepository.save(user);
    }

    public User details(String name){
       return userRepository.findByName(name);
    }
}
