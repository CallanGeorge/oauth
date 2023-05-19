package com.example.oauth.config;

import com.example.oauth.model.User;
import com.example.oauth.repository.UserRepository;
import com.example.oauth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableWebSecurity
public class CustomSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {

        String redirectURL = null;

        if(authentication.getPrincipal() instanceof DefaultOAuth2User){
            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
            System.out.println(userDetails);

            String username = userDetails.getAttribute("name");

            if(userRepository.findByName(username) == null){
                // set id and email here too
                User user = new User();
                user.setName(username);
                userService.save(user);


            }



        } redirectURL = "http://localhost:3000/home";

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectURL);


    }
}
