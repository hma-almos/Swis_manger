package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.MatchEntity;
import com.example.demo.entity.PlayerEntity;

import java.util.List;

@Repository
public interface MatchRepo extends BaseRepo<MatchEntity>{
    @Query("SELECT m FROM MatchEntity m WHERE (m.player1.id = :playerId OR m.player2.id = :playerId) AND m.tournament.id = :tournamentId")
    List<MatchEntity> findMatchesByPlayerAndTournament(
            @Param("playerId") Integer playerId,
            @Param("tournamentId") Integer tournamentId
    );
}
