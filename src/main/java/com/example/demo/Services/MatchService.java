package com.example.demo.Services;

import com.example.demo.dto.MatchDto;
import com.example.demo.entity.MatchEntity;
import com.example.demo.mapper.MatchMapper;
import com.example.demo.repo.MatchRepo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MatchService extends BaseService<MatchRepo, MatchEntity, MatchMapper, MatchDto> {
    public List<MatchEntity> getMatchesForPlayer(int playerId,int tournmentID){
        return repo.findMatchesByPlayerAndTournament(playerId,tournmentID);
    }

}
