package com.example.oauth;


import com.example.oauth.model.User;
import com.example.oauth.service.EventService;
import com.example.oauth.service.UserService;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {

    private final UserService userService;

@Autowired
     EventService eventService;

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/logout")
    public RedirectView logout() {
        return new RedirectView("http://localhost:3000");
    }


    @GetMapping("/token/{date}")
    public ResponseEntity<List<Event>> token(
        @PathVariable LocalDate date,
        OAuth2AuthenticationToken authenticationToken
    ) {

        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
            );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        try {
            List<Event> events = eventService.getPrimaryUserEvents(accessToken, date);

            return ResponseEntity.ok(events);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @GetMapping("/auth")
    public RedirectView auth(
        @ModelAttribute("users")
        User user
    ) {

        return new RedirectView("http://localhost:3000/home");
    }

    @GetMapping("/user")
    public User user(@AuthenticationPrincipal OAuth2User principal) {
        return userService.details(principal.getAttribute("given_name"));
    }


}
