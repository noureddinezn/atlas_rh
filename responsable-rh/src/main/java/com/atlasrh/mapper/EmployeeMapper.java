package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Employee;
import com.atlasrh.dto.EmployeeResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface EmployeeMapper {
    @Mapping(target="departmentId", source="department.id")
    @Mapping(target="departmentName", source="department.name")
    @Mapping(target="managerId", source="manager.id")
    EmployeeResponse toResponse(Employee entity);
}
