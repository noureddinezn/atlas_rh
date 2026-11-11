package com.atlasrh.dto;

import java.time.LocalDate;
import com.atlasrh.entity.ContractType;
public record ContractResponse(Long id, Long employeeId, ContractType type, LocalDate startDate,
    LocalDate endDate, LocalDate terminationDate, boolean active) {}
