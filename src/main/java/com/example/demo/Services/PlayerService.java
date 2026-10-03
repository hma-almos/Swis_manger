package com.example.demo.Services;

import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.PlayerEntity;
import com.example.demo.mapper.PlayerMapper;
import com.example.demo.repo.PlayerRepo;
import org.springframework.stereotype.Service;

@Service
public class PlayerService extends BaseService<PlayerRepo, PlayerEntity, PlayerMapper, PlayerDto> {

}
