package com.atlasrh.api.dto.request;

import com.atlasrh.api.entity.enums.ContractType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractRequestDTO(

        @NotNull(message = "Contract type is required (CDI, CDD, STAGE)")
        ContractType type,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Gross salary is required")
        @Positive(message = "Gross salary must be strictly positive")
        BigDecimal grossSalary,

        @NotNull(message = "Employee ID is required")
        Long employeeId
) {}