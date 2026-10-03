package com.example.demo.mapper;

import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.PlayerEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component

public class PlayerMapper implements BaseMapper<PlayerEntity, PlayerDto>{

    @Override
    public PlayerEntity mapToEntinity(PlayerDto dto) {
        if(dto==null)
            return null;
        return new PlayerEntity(dto.id(),dto.name(), dto.rating(), dto.score(), dto.byeReceived());
    }

    @Override
    public PlayerDto mapToDto(PlayerEntity entity) {
        if(entity==null)
            return null;
        return new PlayerDto(entity.getId(),entity.getName(),entity.getRating(),entity.getScore(),entity.isByeReceived(),new HashMap<>());
    }
}
