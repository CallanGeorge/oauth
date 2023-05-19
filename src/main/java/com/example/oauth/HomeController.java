package com.example.oauth;



import com.example.oauth.model.User;
import com.example.oauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@CrossOrigin( origins = "http://localhost:3000")
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth")
    public void auth(@ModelAttribute("users")
        User user) {
        System.out.println("User"+user);
    }

    @GetMapping("/user")
    public User user(@AuthenticationPrincipal OAuth2User principal) {
        return userService.details(principal.getAttribute("name")) ;
    }


}
