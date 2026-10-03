package com.example.demo.mapper;

import com.example.demo.entity.BaseEntity;

public interface BaseMapper<E extends BaseEntity,D> {
    E mapToEntinity(D dto);
    D mapToDto(E entity);

}
