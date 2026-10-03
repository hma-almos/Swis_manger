package com.example.demo.Services;

import com.example.demo.dto.RoundDto;
import com.example.demo.entity.RoundEntity;
import com.example.demo.mapper.RoundMapper;
import com.example.demo.repo.RoundRepo;
import org.springframework.stereotype.Service;

@Service
public class RoundService extends BaseService<RoundRepo, RoundEntity, RoundMapper, RoundDto> {
}
