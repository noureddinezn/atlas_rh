package com.atlasrh.api.mapper;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "leaveRequests", ignore = true)
    Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerFirstName", source = "manager.firstName")
    @Mapping(target = "managerLastName", source = "manager.lastName")
    EmployeeResponseDTO toResponseDto(Employee entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "leaveRequests", ignore = true)
    void updateEntityFromDto(EmployeeRequestDTO dto, @MappingTarget Employee entity);
}
