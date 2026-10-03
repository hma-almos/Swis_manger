package com.example.demo.Services;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.BaseEntity;
import com.example.demo.repo.BaseRepo;


public class BaseService<R extends BaseRepo<E>,E extends BaseEntity,M extends BaseMapper<E,D>,D> {
	@Autowired
	R repo;
	@Autowired
	M mapper;
	public List<E> selectAll() {
		return repo.findAll();
	}
	public D insert(D entity) {
		return mapper.mapToDto(repo.save(mapper.mapToEntinity(entity)));
	}
	public void delete(E entity) {
		repo.delete(entity);
	}
	public D update(D entity) {
		return mapper.mapToDto(repo.save(mapper.mapToEntinity(entity)));
	}
	public D selectById(int id) {
		return mapper.mapToDto(repo.findById(id).orElse(null));
	}
	public List<D> insertAll(List<D> entities) {
		return repo.saveAll(
				entities
						.stream().map(e->mapper.mapToEntinity(e)).toList())
						.stream().map(e->mapper.mapToDto(e)).toList();
	}
	public List<D> updateAll(List<D> entities) {
		return repo.saveAll(
						entities
								.stream().map(e->mapper.mapToEntinity(e)).toList())
				.stream().map(e->mapper.mapToDto(e)).toList();
	}

}
