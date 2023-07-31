package com.example.oauth.controller;


import com.example.oauth.model.EventDetails;
import com.example.oauth.model.GenericResponse;
import com.example.oauth.model.User;
import com.example.oauth.service.EventService;
import com.example.oauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/api/v1")
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
    public ResponseEntity<List<String>> token(
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
            List<EventDetails> events = eventService.getEvents(accessToken, date, "primary");
            List<String> eventTimes = events.stream()
                .map(EventDetails::getTime)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventTimes);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @GetMapping("/token/whiffwhaff/{date}")
    public ResponseEntity<List<String>> whiffWhaffCalendar(
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
            List<EventDetails> events = eventService.getEvents(accessToken, date, "service_whiffwhaff@xdesign.com");
            List<String> eventTimes = events.stream()
                .map(EventDetails::getTime)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventTimes);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @GetMapping("/token/{date}/{name}")
    public ResponseEntity<List<String>> token(
        @PathVariable LocalDate date, @PathVariable String name,
        OAuth2AuthenticationToken authenticationToken
    ) {

        User user = userService.getByUsername(name);



        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
            );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        try {
            System.out.println(user.getEmail());
            List<EventDetails> events = eventService.getEvents(accessToken, date, user.getEmail());
            List<String> eventTimes = events.stream()
                .map(EventDetails::getTime)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventTimes);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @GetMapping("/check/{eventId}/{email}/{challengedEmail}")
    public GenericResponse checkEvent ( OAuth2AuthenticationToken authenticationToken, @PathVariable long eventId, @PathVariable String email, @PathVariable String challengedEmail) throws IOException {


        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
            );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        GenericResponse message = eventService.checkEvent(accessToken, email, challengedEmail, eventId);

        System.out.println(message);

        return message;

    }

    @GetMapping("/auth")
    public RedirectView auth(
        @ModelAttribute("users")
        User user
    ) {

        return new RedirectView("http://localhost:3000/home");
    }

    @GetMapping("/users")
    Page<User> getUsers( Pageable
        page){
        return userService.getUsers(page);
    }

    @GetMapping("/user")
    public User user(@AuthenticationPrincipal OAuth2User principal) {
        return userService.details(principal.getAttribute("given_name"));
    }

    @GetMapping("/users/{username}")
    User getByUsername(@PathVariable String username){
        User user = userService.getByUsername(username);
        return user;

    }


}
