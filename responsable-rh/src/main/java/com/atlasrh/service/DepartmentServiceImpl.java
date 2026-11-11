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
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class DepartmentServiceImpl {
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    private final DepartmentMapper mapper;
    public Department require(Long id) { return departments.findById(id).orElseThrow(() -> new NotFoundException("Department not found: " + id)); }
    public DepartmentResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<DepartmentResponse> list(Pageable p) { return departments.findAll(p).map(mapper::toResponse); }
    @Transactional
    public DepartmentResponse create(DepartmentRequest r) { return save(new Department(), r); }
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest r) { return save(require(id), r); }
    private DepartmentResponse save(Department d, DepartmentRequest r) {
        String name = r.name().strip();
        if (d.getId() == null ? departments.existsByNameIgnoreCase(name) : departments.existsByNameIgnoreCaseAndIdNot(name, d.getId()))
            throw new ConflictException("Department name already exists");
        d.setName(name); d.setDescription(r.description());
        return mapper.toResponse(departments.saveAndFlush(d));
    }
    @Transactional
    public void delete(Long id) {
        Department d = require(id);
        if (employees.existsByDepartmentId(id)) throw new ConflictException("Department contains employees");
        departments.delete(d); departments.flush();
    }
}
