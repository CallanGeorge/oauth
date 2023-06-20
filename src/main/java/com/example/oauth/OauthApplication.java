package com.example.oauth;

import com.example.oauth.model.User;
import com.example.oauth.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.IntStream;

@SpringBootApplication
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    // DO NOT NEED THIS AS IT NOW IS STORED IN DB

        @Bean
        CommandLineRunner run (UserService userService){
            return (args) -> {
                IntStream.rangeClosed(1,1).mapToObj(i -> {
                    User user = new User();
                    user.setEmail("francesco.lamarca@xdesign.com");
                    user.setScore(0);
                    user.setName("Frankie");

                    return user;
                })
                    .forEach(userService::save);
            };
        }

}
