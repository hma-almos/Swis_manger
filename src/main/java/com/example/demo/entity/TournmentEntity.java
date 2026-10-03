package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class TournmentEntity extends BaseEntity{
    private int roundsCount;
    @OneToMany
    @JoinColumn(name = "rounds")

    private List<RoundEntity> rounds;
    @OneToMany
    @JoinColumn(name="players")
    private List<PlayerEntity> players;


    public TournmentEntity() {
    }

    public TournmentEntity(Integer id, int roundsCount, List<RoundEntity> rounds, List<PlayerEntity>players) {
        super(id);
        this.roundsCount = roundsCount;
        this.rounds = rounds;
        this.players = players;
    }

    public int getRoundsCount() {
        return roundsCount;
    }

    public void setRoundsCount(int roundsCount) {
        this.roundsCount = roundsCount;
    }

    public void setRounds(List<RoundEntity> rounds) {
        this.rounds = rounds;
    }
    public void addRound(RoundEntity round){
        if(this.rounds==null)
            rounds=new ArrayList<>();
        rounds.add(round);
    }

    public void setPlayers(List<PlayerEntity>players) {
        this.players = players;
    }

    public List<RoundEntity> getRounds() {
        return rounds;
    }

    public List<PlayerEntity>getPlayers() {
        return players;
    }
}
