package com.example.demo.Services;

import com.example.demo.dto.TournmentDto;
import com.example.demo.entity.PlayerEntity;
import com.example.demo.entity.TournmentEntity;
import com.example.demo.mapper.TournmentMapper;
import com.example.demo.repo.PlayerRepo;
import com.example.demo.repo.TurnmentRepo;
import org.springframework.stereotype.Service;

@Service
public class TournmentService extends BaseService<TurnmentRepo, TournmentEntity, TournmentMapper, TournmentDto> {
}
