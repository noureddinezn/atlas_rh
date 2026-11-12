package com.atlasrh.api.service.impl;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.entity.Department;
import com.atlasrh.api.entity.Employee;
import com.atlasrh.api.exception.ResourceNotFoundException;
import com.atlasrh.api.mapper.EmployeeMapper;
import com.atlasrh.api.repository.DepartmentRepository;
import com.atlasrh.api.repository.EmployeeRepository;
import com.atlasrh.api.service.ContractService;
import com.atlasrh.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final ContractService contractService;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDto) {
        log.info("Creating employee with matricule: {}", requestDto.matricule());

        if (employeeRepository.existsByMatriculeIgnoreCase(requestDto.matricule())) {
            throw new IllegalStateException("Matricule already exists: " + requestDto.matricule());
        }

        if (employeeRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new IllegalStateException("Email already exists: " + requestDto.email());
        }

        Employee employee = employeeMapper.toEntity(requestDto);
        setDepartmentAndManager(employee, requestDto);

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDto) {
        log.info("Updating employee ID: {}", id);

        Employee employee = getEmployee(id);

        boolean matriculeChanged = !employee.getMatricule().equalsIgnoreCase(requestDto.matricule());
        boolean emailChanged = !employee.getEmail().equalsIgnoreCase(requestDto.email());

        if (matriculeChanged
                && employeeRepository.existsByMatriculeIgnoreCase(requestDto.matricule())) {
            throw new IllegalStateException("Matricule already exists: " + requestDto.matricule());
        }

        if (emailChanged
                && employeeRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new IllegalStateException("Email already exists: " + requestDto.email());
        }

        if (requestDto.managerId() != null && requestDto.managerId().equals(id)) {
            throw new IllegalArgumentException("An employee cannot be their own manager");
        }

        employeeMapper.updateEntityFromDto(requestDto, employee);
        setDepartmentAndManager(employee, requestDto);

        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = getEmployee(id);
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public Page<EmployeeResponseDTO> getAllEmployees(String name, Long departmentId, Pageable pageable) {
        Page<Employee> employeesPage;
        boolean hasName = name != null && !name.isBlank();

        if (departmentId != null && hasName) {
            employeesPage = employeeRepository.findByDepartmentIdAndLastNameContainingIgnoreCase(departmentId, name, pageable);
        } else if (departmentId != null) {
            employeesPage = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else if (hasName) {
            employeesPage = employeeRepository.findByLastNameContainingIgnoreCase(name, pageable);
        } else {
            employeesPage = employeeRepository.findAll(pageable);
        }

        return employeesPage.map(employee -> employeeMapper.toResponseDto(employee));
    }

    @Override
    public List<EmployeeResponseDTO> getManagerTeam(Long managerId) {
        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager not found with ID: " + managerId);
        }

        List<Employee> team = employeeRepository.findByManagerId(managerId);
        return team.stream()
                .map(employee -> employeeMapper.toResponseDto(employee))
                .toList();
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    private void setDepartmentAndManager(Employee employee, EmployeeRequestDTO dto) {
        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.departmentId()));
        employee.setDepartment(department);

        Employee manager = null;
        if (dto.managerId() != null) {
            manager = employeeRepository.findById(dto.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + dto.managerId()));
        }
        employee.setManager(manager);
    }

}
