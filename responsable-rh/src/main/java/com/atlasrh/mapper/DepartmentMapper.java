package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Department;
import com.atlasrh.dto.DepartmentResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface DepartmentMapper {
    DepartmentResponse toResponse(Department entity);
}
