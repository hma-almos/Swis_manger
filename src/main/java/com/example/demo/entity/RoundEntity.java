package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table
public class RoundEntity extends BaseEntity{
	@OneToMany
	@JoinColumn(name = "matches")
	private List<MatchEntity> matches;
	private int round;
	public RoundEntity() {
	}
	public RoundEntity(Integer id,  List<MatchEntity> matches, int round) {
		super(id);
		this.matches = matches;
		this.round = round;
	}
	public List<MatchEntity> getMatches() {
		return matches;
	}
	public void addMatch(MatchEntity match){
		if(this.matches==null)
			matches=new ArrayList<>();
		matches.add(match);
	}
	public void setMatches(List<MatchEntity> matches) {
		this.matches = matches;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
}
