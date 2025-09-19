package com.noahgeerts.taskboard.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    private ModelMapper modelMapper;

    public Mapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <T> T map(Object obj, Class<T> c) {
        return modelMapper.map(obj, c);
    }
}
