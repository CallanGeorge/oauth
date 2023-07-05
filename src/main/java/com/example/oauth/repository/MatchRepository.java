package com.example.oauth.repository;

import com.example.oauth.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Match getMatchById(long id);

    void deleteById(long id);


    Page<Match> findAllByPlayer1VotedIsTrueAndPlayer2VotedIsTrueOrderByIdDesc(Pageable page);


    List<Match>findAllByPlayer1AndWinnerIsNull(String username);

    List<Match>findAllByPlayer2AndWinnerIsNull(String username);

    List<Match>findAllByPlayer1AndWinnerIsNotNull(String username);

    List<Match>findAllByPlayer2AndWinnerIsNotNull(String username);


    List<Match>findAllByPlayer1(String username);

    List<Match>findAllByPlayer2(String username);


}
