package com.example.oauth.service;

import com.example.oauth.model.User;
import com.example.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public User save(User user){
        System.out.println(user);
       return userRepository.save(user);
    }


    public Page<User> getUsers(Pageable page){

        return userRepository.findAllByOrderByScoreDesc(page);
    }
    public User getByUsername(String username){

        User inDB = userRepository.findByName(username);
        if(inDB == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return inDB;
    }


    public User details(String name){
       return userRepository.findByName(name);
    }
}
