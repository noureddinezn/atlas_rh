package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Contract;
import com.atlasrh.dto.ContractResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface ContractMapper {
    @Mapping(target="employeeId", source="employee.id")
    ContractResponse toResponse(Contract entity);
}
