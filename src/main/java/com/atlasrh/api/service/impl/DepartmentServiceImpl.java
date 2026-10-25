package com.atlasrh.api.service.impl;

import com.atlasrh.api.dto.request.DepartmentRequestDTO;
import com.atlasrh.api.dto.response.DepartmentResponseDTO;
import com.atlasrh.api.entity.Department;
import com.atlasrh.api.exception.BusinessConflictException;
import com.atlasrh.api.exception.ResourceNotFoundException;
import com.atlasrh.api.mapper.DepartmentMapper;
import com.atlasrh.api.repository.DepartmentRepository;
import com.atlasrh.api.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO requestDto) {
        log.info("Creating department with name: {}", requestDto.name());

        if (departmentRepository.existsByNameIgnoreCase(requestDto.name())) {
            throw new BusinessConflictException("Department with name '" + requestDto.name() + "' already exists");
        }

        Department department = departmentMapper.toEntity(requestDto);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO requestDto) {
        log.info("Updating department ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        if (!department.getName().equalsIgnoreCase(requestDto.name())
                && departmentRepository.existsByNameIgnoreCase(requestDto.name())) {
            throw new BusinessConflictException("Department with name '" + requestDto.name() + "' already exists");
        }

        departmentMapper.updateEntityFromDto(requestDto, department);
        Department updated = departmentRepository.save(department);
        return departmentMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        return departmentMapper.toResponseDto(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new BusinessConflictException("Cannot delete department because it contains assigned employees");
        }

        departmentRepository.delete(department);
    }
}