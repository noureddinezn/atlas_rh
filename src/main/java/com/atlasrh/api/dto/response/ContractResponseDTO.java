package com.atlasrh.api.dto.response;

import com.atlasrh.api.entity.enums.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponseDTO(
        Long id,
        ContractType type,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal grossSalary,
        Boolean active,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        String employeeMatricule) {
}