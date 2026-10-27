package com.atlasrh.api.mapper;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerFirstName", source = "manager.firstName")
    @Mapping(target = "managerLastName", source = "manager.lastName")
    EmployeeResponseDTO toResponseDto(Employee entity);

    void updateEntityFromDto(EmployeeRequestDTO dto, @MappingTarget Employee entity);
}