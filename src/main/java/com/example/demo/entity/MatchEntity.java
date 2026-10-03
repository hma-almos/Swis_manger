package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table
public class MatchEntity extends BaseEntity{
	private int round;
	@ManyToOne
	@JoinColumn(name = "player1_id")
	private PlayerEntity player1;
	@ManyToOne
	@JoinColumn(name = "player2_id")
	private PlayerEntity player2;
	@ManyToOne
	@JoinColumn(name = "tournament_id")
	private TournmentEntity tournament;
	//if 0 then black win if 1 then white win
	@Column(nullable = true)
	private Boolean winPlayer1;
	//true mean white
	private boolean colorPlayer1;
	public MatchEntity(Integer id, int round, PlayerEntity player1, PlayerEntity player2, Boolean winPlayer1, boolean colorPlayer1,TournmentEntity tournament) {
		super(id);
		this.round = round;
		this.player1 = player1;
		this.player2 = player2;
		this.winPlayer1 = winPlayer1;
		this.colorPlayer1 = colorPlayer1;
		this.tournament=tournament;
	}
	public MatchEntity() {
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public PlayerEntity getPlayer1() {
		return player1;
	}
	public void setPlayer1(PlayerEntity player1) {
		this.player1 = player1;
	}
	public PlayerEntity getPlayer2() {
		return player2;
	}
	public void setPlayer2(PlayerEntity player2) {
		this.player2 = player2;
	}
	public Boolean isWinPlayer1() {
		return winPlayer1;
	}
	public void setWinPlayer1(Boolean winPlayer1) {
		this.winPlayer1 = winPlayer1;
	}
	public boolean isColorPlayer1() {
		return colorPlayer1;
	}
	public void setColorPlayer1(boolean colorPlayer1) {
		this.colorPlayer1 = colorPlayer1;
	}

	public TournmentEntity getTournament() {
		return tournament;
	}

	public void setTournament(TournmentEntity tournament) {
		this.tournament = tournament;
	}
}
