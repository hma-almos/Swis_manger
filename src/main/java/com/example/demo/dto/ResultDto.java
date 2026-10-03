package com.example.demo.dto;

public record ResultDto(
        PlayerDto playerDto,
        boolean color,
        boolean win
) {
}
