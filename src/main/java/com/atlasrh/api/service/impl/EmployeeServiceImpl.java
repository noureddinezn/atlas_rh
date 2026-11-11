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

//    public List<String> getEmployeeNames(){
//        return employeeRepository.findAll().stream().
//        .map(e -> e.getFirstNam()).toList();
//    }

//    public List<EmployeeResponseDTO> getEmployeewithManger {
//        return employeeRepository.findAll().stream().filter(e -> e.getManager() != null)
//                .map(e -> e.employeeMapper.toResponseDto(e))
//                .toList();
//    }
//
//    public Long countEmployeeWithManger(){
//        return employeeRepository.findAll().stream()
//                .filter(e-> e.getManger() != null)
//                .count();
//    }


//    public Employee FinByfirst(String email){
//        return employeeRepository.findAll().stream()
//                .filter(emp ->emp.getEmail().equalsIgnoreCase(email))
//                .findFirst().orElseThrow( new ResourceNotFoundException("employee not found"));
//    }
    // jib ga3 les contract dial ga3 les class employee fi la list wehda
//    public List<Contract> getAllEmployeeContracts(){
//        return employeeRepository.findAll().stream()
//                .filter(e -> e.getContracts() != null)
//                .flatMap(e -> e.getContracts().stream())
//                .toList();
//    }

//    public BigDecimal getTotalActiveSalary(){
//        return employeeRepository.findAll()
//                .stream()
//                .filter(e ->e.getContract() != null)
//                .flatMap(e ->e.getContracts().stream())
//                .filter(e ->e.getContracts.equals(c.getActive()))
//                .map(c  -> c.getGrossSalary())
//                .reduce(BigDecimal.ZERO, BigDecimal:: add);
//    }

//    public int countAllContracts(){
//        return employeeRepository.findAll().stream()
//                .filter(e -> e.getContracts() != null)
//                .maptoInt(e -> e.getContracts().size())
//                .sum();
//    }

//    public List<EmployeeResponseDTO> getManagerTeam() {
//        return employeeRepository.findAll().stream()
//                .filter(employee -> employee.getManger() != null)
//                .map(employee -> employeeMapper.toResponseDto(employee))
//                .toList();
//    }

//    public List<EmployeeResponseDTO> getEmployee(){
//        return employeeRepository.finAll().stream()
//                .map(e -> employeeMaper.toResponseDto(e))
//                .toList();
//    }
//
//    public List<EmployeeResponseDTO> getEmployeeByName{
//        return employeeRepository.findAll().stream()
//                .filter(e -> e.getFirstName().equalsIgnoreCase(name))
//                .map(e -> employeeMapper().toResponseDto())
//                .toList();
//    }

    // jib les employyee lieandhom manger
//    public List<EmployeeRepository> getEmployeeWithManger(Long departmentId) {
//        return employeeRepository.finAll().stream()
//                .filter(e -> e.getDepartment() != null)
//                .filter(e -> e.getDepartment().getId().equals(departmentId))
//                .filter(e->e.getManger() != null)
//                .map(e -> employeeMapper().toResponseDto(e))
//                .toList()
//    }
//    public EmployeeResponseDto getEmployeeByEmail(String email){
//        return  employeeRepository.findAll().stream()
//                .filter(e ->e.getEmail().equalsIgnore(email))
//                .map(e -> employeeMapper.toResponseDto())
//                .findFirst()
//                .orElseThrow();
//    }

//    public List<EmployeeResponseDTO> getFirstThreeTeamNumber(Long managerId){
//        return employeeRepository.findAll().stream()
//                .filter(e ->e.getManger()!= null)
//                .filter(e -> e.getManger().getId().equals(managerId))
//                .sorted(Comparator.comparing(e ->e.getFirstName()))
//                .limit(3)
//                .map(e ->e.employeeMapper.toResponseDto(e))
//                .toList()
//    }

    public List<Employee> troiEmployee(){
        return employeeRepository.findAll().stream()
                .sorted(Comparator.comparing(e -> e.getHireDate()))
                .limit(3)
                .toList();
    }

}
