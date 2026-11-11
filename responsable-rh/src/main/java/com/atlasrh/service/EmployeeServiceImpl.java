package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EmployeeServiceImpl {
    private final EmployeeRepository employees;
    private final ContractRepository contracts;
    private final DepartmentServiceImpl departments;
    private final EmployeeMapper mapper;
    public Employee require(Long id) { return employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found: " + id)); }
    public EmployeeResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<EmployeeResponse> search(String name, Long departmentId, Pageable p) {
        Specification<Employee> spec = (r,q,b) -> b.conjunction();
        if (name != null && !name.isBlank()) {
            String value = "%" + name.strip().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
            spec = spec.and((r,q,b) -> b.like(b.lower(b.concat(b.concat(r.get("firstName"), " "), r.get("lastName"))), value, '!'));
        }
        if (departmentId != null) { departments.require(departmentId); spec = spec.and((r,q,b) -> b.equal(r.get("department").get("id"), departmentId)); }
        return employees.findAll(spec, p).map(mapper::toResponse);
    }
    public Page<EmployeeResponse> team(Long id, Pageable p) { require(id); return employees.findByManagerId(id, p).map(mapper::toResponse); }
    @Transactional
    public EmployeeResponse create(EmployeeRequest r) { return save(new Employee(), r); }
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest r) { return save(require(id), r); }
    private EmployeeResponse save(Employee e, EmployeeRequest r) {
        String matricule = r.matricule().strip(); String email = r.email().strip().toLowerCase(Locale.ROOT);
        Long id = e.getId();
        if (id == null ? employees.existsByMatriculeIgnoreCase(matricule) : employees.existsByMatriculeIgnoreCaseAndIdNot(matricule,id)) throw new ConflictException("Matricule already exists");
        if (id == null ? employees.existsByEmailIgnoreCase(email) : employees.existsByEmailIgnoreCaseAndIdNot(email,id)) throw new ConflictException("Email already exists");
        if (id != null && id.equals(r.managerId())) throw new BadRequestException("Employee cannot manage themselves");
        e.setDepartment(departments.require(r.departmentId()));
        e.setManager(r.managerId() == null ? null : require(r.managerId()));
        e.setMatricule(matricule); e.setEmail(email); e.setFirstName(r.firstName().strip()); e.setLastName(r.lastName().strip());
        return mapper.toResponse(employees.saveAndFlush(e));
    }
    @Transactional
    public void delete(Long id) {
        Employee e = require(id);
        if (employees.existsByManagerId(id)) throw new ConflictException("Employee manages a team");
        if (contracts.existsByEmployeeId(id)) throw new ConflictException("Employee has contract history");
        employees.delete(e); employees.flush();
    }
}
