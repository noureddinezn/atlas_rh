package com.atlasrh.api.service;

import com.atlasrh.api.dto.request.DepartmentRequestDTO;
import com.atlasrh.api.dto.response.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDTO createDepartment(DepartmentRequestDTO requestDto);

    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO requestDto);

    DepartmentResponseDTO getDepartmentById(Long id);

    List<DepartmentResponseDTO> getAllDepartments();

    void deleteDepartment(Long id);
}
