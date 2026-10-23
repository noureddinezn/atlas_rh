package com.atlasrh.api.mapper;

import com.atlasrh.api.dto.request.DepartmentRequestDTO;
import com.atlasrh.api.dto.response.DepartmentResponseDTO;
import com.atlasrh.api.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Department toEntity(DepartmentRequestDTO dto);

    @Mapping(target = "employeeCount", expression = "java(entity.getEmployees() != null ? entity.getEmployees().size() : 0)")
    DepartmentResponseDTO toResponseDto(Department entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromDto(DepartmentRequestDTO dto, @MappingTarget Department entity);
}
