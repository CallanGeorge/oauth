package com.example.oauth.controller;

import com.example.oauth.model.CreateMatchRequest;
import com.example.oauth.model.GenericResponse;
import com.example.oauth.model.Match;
import com.example.oauth.model.User;
import com.example.oauth.repository.UserRepository;
import com.example.oauth.service.EventService;
import com.example.oauth.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class MatchController {

    @Autowired
    MatchService matchService;

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

    @GetMapping("/all-matches")
    Page<Match> getAllMatches (Pageable page){
        return matchService.getMatches(page);
    }

    @GetMapping("/match/{id}")
    Match getSingleMatch (@PathVariable long id){
        Match match = matchService.getSingleMatch(id);
        return match;
    }

    @GetMapping("/matches/{username}")
    List<Match> getUserMatches ( @PathVariable String username){
        List<Match> matches = matchService.getOngoingUserMatches(username);
        return matches;

    }


    @GetMapping("/final-matches/{username}")
    List<Match> getFinishedUserMatches(@PathVariable String username){
        List<Match> matches = matchService.getFinalUserMatches(username);
        return matches;
    }

    @GetMapping("/invites/{username}")
    List<Match> getInvites(@PathVariable String username){
        List<Match> invites = matchService.getUserInvites(username);
        return invites;
    }


    @PutMapping("/matches/{id}")
    Match acceptMatch (@PathVariable long id){
        Match match = matchService.accept(id);

        return match;
    }


    @PostMapping("/matches")
    GenericResponse createMatch (@Valid @RequestBody
        CreateMatchRequest matchRequest,  OAuth2AuthenticationToken authenticationToken) throws GeneralSecurityException, IOException {


        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
            );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();


        User player1 =  userRepository.findByEmail(matchRequest.getPlayer1());
      User player2 =  userRepository.findByEmail(matchRequest.getPlayer2());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(matchRequest.getMatchTime(), formatter);

      final boolean result = eventService.sendCalendarInvite(accessToken, player1, player2, dateTime);

      if(result){
          Match match = new Match();
          match.setPlayer1(matchRequest.getPlayer1());
          match.setPlayer2(matchRequest.getPlayer2());

          matchService.save(match);
          return new GenericResponse("Match created!");
      }else{
          return new GenericResponse("Issues with event");
      }

    }

    @PutMapping("/result/{id}")
    GenericResponse setResult (@PathVariable long id, @RequestBody
    Map<String, String> data) {
        Match match = matchService.setResult(id, data);

        return new GenericResponse(data.get("user"));
    }
}
