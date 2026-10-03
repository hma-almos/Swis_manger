package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BaseEntity;

import jakarta.persistence.MappedSuperclass;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepo<E extends BaseEntity> extends JpaRepository<E, Integer>{

}
