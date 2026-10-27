package com.atlasrh.api.service;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDto);

    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDto);

    EmployeeResponseDTO getEmployeeById(Long id);

    Page<EmployeeResponseDTO> getAllEmployees(String name, Long departmentId, Pageable pageable);

    List<EmployeeResponseDTO> getManagerTeam(Long managerId);
}
