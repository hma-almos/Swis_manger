package com.example.demo.entity;

import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.ResultDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Map;

@Entity
@Table
public class PlayerEntity extends BaseEntity {

	private String name;
	private int rating;
	private int score;
	private boolean ByeReceived;

	public PlayerEntity(Integer id, String name, int rating, int score, boolean byeReceived) {
		super(id);
		this.name = name;
		this.rating = rating;
		this.score = score;
		ByeReceived = byeReceived;
	}

	public PlayerEntity() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isByeReceived() {
		return ByeReceived;
	}

	public void setByeReceived(boolean byeReceived) {
		ByeReceived = byeReceived;
	}
}
