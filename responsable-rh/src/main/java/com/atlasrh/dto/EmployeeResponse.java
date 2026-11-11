package com.atlasrh.dto;

public record EmployeeResponse(Long id, String matricule, String email, String firstName, String lastName, Long departmentId, String departmentName, Long managerId) {}
