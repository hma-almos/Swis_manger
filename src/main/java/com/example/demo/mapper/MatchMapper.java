package com.example.demo.mapper;

import com.example.demo.dto.MatchDto;
import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.TournmentDto;
import com.example.demo.entity.MatchEntity;
import com.example.demo.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper implements BaseMapper<MatchEntity, MatchDto>{
    @Autowired
    PlayerMapper mapper;
    @Autowired
    @Lazy
    TournmentMapper Tournmentmapper;
    @Override
    public MatchEntity mapToEntinity(MatchDto dto) {
        return new MatchEntity(dto.id(),dto.round(),
                mapper.mapToEntinity(dto.player1()),
                mapper.mapToEntinity(dto.player2())
                ,dto.winPlayer1(),dto.colorPlayer1(),
                Tournmentmapper.mapToEntinity(dto.tournmentDto()));
    }

    @Override
    public MatchDto mapToDto(MatchEntity entity) {
        return new MatchDto(entity.getId(),entity.getRound(),
                mapper.mapToDto(entity.getPlayer1()),
                mapper.mapToDto(entity.getPlayer2())
                ,entity.isWinPlayer1(),entity.isColorPlayer1(),null);
    }


}
