package com.atlasrh.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequestDTO(

        @NotBlank(message = "Matricule is required") @Size(max = 50, message = "Matricule must not exceed 50 characters") String matricule,

        @NotBlank(message = "First name is required") @Size(max = 100, message = "First name must not exceed 100 characters") String firstName,

        @NotBlank(message = "Last name is required") @Size(max = 100, message = "Last name must not exceed 100 characters") String lastName,

        @NotBlank(message = "Email is required") @Email(message = "Email must be a valid format") @Size(max = 150, message = "Email must not exceed 150 characters") String email,

        @NotBlank(message = "Position is required") @Size(max = 100, message = "Position must not exceed 100 characters") String position,

        @NotNull(message = "Hire date is required") @PastOrPresent(message = "Hire date cannot be in the future") LocalDate hireDate,

        @NotNull(message = "Department ID is required") Long departmentId,

        Long managerId) {
}