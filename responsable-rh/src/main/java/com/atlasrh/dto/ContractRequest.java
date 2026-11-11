package com.atlasrh.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import com.atlasrh.entity.ContractType;
public record ContractRequest(@NotNull @Positive Long employeeId, @NotNull ContractType type,
    @NotNull LocalDate startDate, LocalDate endDate) {}
