package com.atlasrh.api.dto.response;

import java.time.LocalDate;

public record EmployeeResponseDTO(
        Long id,
        String matricule,
        String firstName,
        String lastName,
        String email,
        String position,
        LocalDate hireDate,
        Long departmentId,
        String departmentName,
        Long managerId,
        String managerFirstName,
        String managerLastName) {
}