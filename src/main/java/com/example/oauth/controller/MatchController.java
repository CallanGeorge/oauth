package com.example.oauth.controller;

import com.example.oauth.model.GenericResponse;
import com.example.oauth.model.Match;
import com.example.oauth.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class MatchController {

    @Autowired
    MatchService matchService;

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
    GenericResponse createMatch (@Valid @RequestBody Match match){
        matchService.save(match);
        return new GenericResponse("Match created!");
    }

    @PutMapping("/result/{id}")
    GenericResponse setResult (@PathVariable long id, @RequestBody
    Map<String, String> data) {
        Match match = matchService.setResult(id, data);

        return new GenericResponse(data.get("user"));
    }
}
