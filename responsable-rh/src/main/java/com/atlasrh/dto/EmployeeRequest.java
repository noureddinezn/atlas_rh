package com.atlasrh.dto;

import jakarta.validation.constraints.*;
public record EmployeeRequest(
    @NotBlank @Size(max=50) String matricule,
    @NotBlank @Email @Size(max=254) String email,
    @NotBlank @Size(max=100) String firstName,
    @NotBlank @Size(max=100) String lastName,
    @NotNull @Positive Long departmentId, @Positive Long managerId) {}
