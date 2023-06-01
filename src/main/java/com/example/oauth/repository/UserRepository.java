package com.example.oauth.repository;

import com.example.oauth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    User findByName(String name);

    User findByEmail(String email);



}
