package com.example.oauth.service;


import com.example.oauth.model.Match;

import com.example.oauth.model.RequestStatus;
import com.example.oauth.model.User;
import com.example.oauth.repository.MatchRepository;
import com.example.oauth.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class MatchService {

    MatchRepository matchRepository;

    UserRepository userRepository;

    public MatchService (MatchRepository matchRepository, UserRepository userRepository){
        super();
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    public void save( Match match){
        matchRepository.save(match);
    }

    public Page<Match> getMatches(Pageable page ){
        return matchRepository.findAllByPlayer1VotedIsTrueAndPlayer2VotedIsTrueOrderByIdDesc(page);
    }

    public Match getSingleMatch (long id){
        Match match = matchRepository.getMatchById(id);

        return match;
    }

    public List<Match> getUserInvites(String username){
        List<Match> invites = matchRepository.findAllByPlayer1(username);
        List<Match> moreInvites = matchRepository.findAllByPlayer2(username);

        invites.removeIf(inv -> inv.getResponse() != RequestStatus.PENDING);
        moreInvites.removeIf(minv -> minv.getResponse() != RequestStatus.PENDING);

        List<Match> allInvites = new ArrayList<Match>(invites);
        allInvites.addAll(moreInvites);

        return allInvites;
    }

    public List<Match> getFinalUserMatches(String username){
        List<Match> matches = matchRepository.findAllByPlayer1AndWinnerIsNotNull(username);
        List<Match> moreMatches = matchRepository.findAllByPlayer2AndWinnerIsNotNull(username);

        List<Match> allMatches = new ArrayList<Match>(matches);
        allMatches.addAll(moreMatches);

        Collections.reverse(allMatches);

        List<Match> finalMatches = new ArrayList<>();


        int length=allMatches.size();

        if(length > 4){
            for (int i = 0; i < 5; i++){
                finalMatches.add(allMatches.get(i));

            }
        } else {
            finalMatches = allMatches;
        }



        return finalMatches;
    }

    public List<Match> getOngoingUserMatches(String username){
        List<Match> matches = matchRepository.findAllByPlayer1AndWinnerIsNull(username);
        List<Match> moreMatches = matchRepository.findAllByPlayer2AndWinnerIsNull(username);

        matches.removeIf(match -> match.getResponse() != RequestStatus.ACCEPTED);
        moreMatches.removeIf(match2 -> match2.getResponse() != RequestStatus.ACCEPTED);

        List<Match> allMatches = new ArrayList<Match>(matches);
        allMatches.addAll(moreMatches);



        return allMatches;
    }

    public Match accept(long id){
        Match inDB = matchRepository.getOne(id);
        inDB.setResponse(RequestStatus.ACCEPTED);

        return matchRepository.save(inDB);
    }

    public Match setResult(long id, Map<String, String> data){
        Match match = matchRepository.getMatchById(id);
        String player1Check = match.getPlayer1();
        String user = data.get("user");



        if(match.getPlayer1().equals(data.get("user"))){
            match.setPlayer1Vote(data.get("vote"));
            match.setPlayer1Voted(true);

            matchRepository.save(match);
        } else{
            match.setPlayer2Vote(data.get("vote"));
            match.setPlayer2Voted(true);

            matchRepository.save(match);
        }

        if(match.getPlayer1Voted() == true && match.getPlayer2Voted() == true){
            if(match.getPlayer1Vote().equals(match.getPlayer2Vote())){
                User winner = userRepository.findByEmail(match.getPlayer2Vote());

                match.setWinner(match.getPlayer1Vote());
                matchRepository.save(match);

                winner.setScore(winner.getScore() + 1);
                userRepository.save(winner);

            } else {

                match.setPlayer2Voted(false);
                match.setPlayer1Voted(false);
                match.setPlayer2Vote(null);
                match.setPlayer1Vote(null);


            }
            return matchRepository.save(match);

        } else{
            return match;
        }

    }
}

