package com.example.demo.dto;

public record MatchDto(
        Integer id,
        int round,
        PlayerDto player1,
        PlayerDto player2,
        Boolean winPlayer1,
        boolean colorPlayer1,
        TournmentDto tournmentDto
    ) {
}
