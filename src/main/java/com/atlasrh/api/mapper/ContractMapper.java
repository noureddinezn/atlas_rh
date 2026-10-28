package com.atlasrh.api.mapper;

import com.atlasrh.api.dto.request.ContractRequestDTO;
import com.atlasrh.api.dto.response.ContractResponseDTO;
import com.atlasrh.api.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    Contract toEntity(ContractRequestDTO dto);

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFirstName", source = "employee.firstName")
    @Mapping(target = "employeeLastName", source = "employee.lastName")
    @Mapping(target = "employeeMatricule", source = "employee.matricule")
    ContractResponseDTO toResponseDto(Contract entity);
}