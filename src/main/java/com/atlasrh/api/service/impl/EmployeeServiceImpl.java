package com.atlasrh.api.service.impl;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.entity.Department;
import com.atlasrh.api.entity.Employee;
import com.atlasrh.api.exception.BadRequestException;
import com.atlasrh.api.exception.BusinessConflictException;
import com.atlasrh.api.exception.ResourceNotFoundException;
import com.atlasrh.api.mapper.EmployeeMapper;
import com.atlasrh.api.repository.DepartmentRepository;
import com.atlasrh.api.repository.EmployeeRepository;
import com.atlasrh.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDto) {
        log.info("Creating employee with matricule: {}", requestDto.matricule());

        if (employeeRepository.existsByMatriculeIgnoreCase(requestDto.matricule())) {
            throw new BusinessConflictException("Matricule already exists: " + requestDto.matricule());
        }

        if (employeeRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new BusinessConflictException("Email already exists: " + requestDto.email());
        }

        Department department = departmentRepository.findById(requestDto.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with ID: " + requestDto.departmentId()));

        Employee employee = employeeMapper.toEntity(requestDto);
        employee.setDepartment(department);

        if (requestDto.managerId() != null) {
            Employee manager = employeeRepository.findById(requestDto.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found with ID: " + requestDto.managerId()));
            employee.setManager(manager);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDto) {
        log.info("Updating employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (!employee.getMatricule().equalsIgnoreCase(requestDto.matricule())
                && employeeRepository.existsByMatriculeIgnoreCase(requestDto.matricule())) {
            throw new BusinessConflictException("Matricule already exists: " + requestDto.matricule());
        }

        if (!employee.getEmail().equalsIgnoreCase(requestDto.email())
                && employeeRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new BusinessConflictException("Email already exists: " + requestDto.email());
        }

        if (requestDto.managerId() != null && requestDto.managerId().equals(id)) {
            throw new BadRequestException("An employee cannot be their own manager");
        }

        Department department = departmentRepository.findById(requestDto.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with ID: " + requestDto.departmentId()));

        employeeMapper.updateEntityFromDto(requestDto, employee);
        employee.setDepartment(department);

        if (requestDto.managerId() != null) {
            Employee manager = employeeRepository.findById(requestDto.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found with ID: " + requestDto.managerId()));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(String name, Long departmentId, Pageable pageable) {
        Page<Employee> employeesPage;

        if (departmentId != null) {
            employeesPage = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else if (name != null && !name.isBlank()) {
            employeesPage = employeeRepository.findByLastNameContainingIgnoreCase(name, pageable);
        } else {
            employeesPage = employeeRepository.findAll(pageable);
        }

        return employeesPage.map(employeeMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getManagerTeam(Long managerId) {
        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager not found with ID: " + managerId);
        }

        return employeeRepository.findByManagerId(managerId)
                .stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }
}