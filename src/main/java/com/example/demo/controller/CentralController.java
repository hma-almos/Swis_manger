package com.example.demo.controller;

import com.example.demo.Services.CenteralService;
import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.TournmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class CentralController {
    @Autowired
    private CenteralService centeralService;
    @PostMapping("/initial")
    public TournmentDto initialMatch(@RequestBody TournmentDto tournmentDto){
        return centeralService.initialMatching(tournmentDto);
    }
    @PostMapping("/next")
    public TournmentDto nextRound(@RequestBody TournmentDto tournmentDto){
        return centeralService.nextRound(tournmentDto);
    }
    @GetMapping("/player/{id}")
    public PlayerDto getPlayerById(@PathVariable int id){
        return centeralService.getPlayerById(id);
    }
    @GetMapping("/tournament/{tournamentId}/player/{playerId}")
    public PlayerDto getPlayerDate(@PathVariable int tournamentId,@PathVariable int playerId){
        return centeralService.getPlayerData(tournamentId,playerId);
    }
}
