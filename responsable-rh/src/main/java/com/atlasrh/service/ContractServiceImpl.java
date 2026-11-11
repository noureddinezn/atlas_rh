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
public class ContractServiceImpl {
    private final ContractRepository contracts;
    private final EmployeeRepository employees;
    private final ContractMapper mapper;
    private Contract require(Long id) { return contracts.findById(id).orElseThrow(() -> new NotFoundException("Contract not found: " + id)); }
    private Employee lockEmployee(Long id) { return employees.findLockedById(id).orElseThrow(() -> new NotFoundException("Employee not found: " + id)); }
    public ContractResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<ContractResponse> history(Long employeeId, Pageable p) {
        if (!employees.existsById(employeeId)) throw new NotFoundException("Employee not found: " + employeeId);
        return contracts.findByEmployeeId(employeeId, p).map(mapper::toResponse);
    }
    @Transactional
    public ContractResponse create(ContractRequest r) {
        if (r.type() != ContractType.CDI && r.endDate() == null) throw new BadRequestException("CDD and STAGE require endDate");
        if (r.endDate() != null && r.endDate().isBefore(r.startDate())) throw new BadRequestException("endDate must be on or after startDate");
        Employee e = lockEmployee(r.employeeId());
        if (contracts.existsByEmployeeIdAndActiveTrue(e.getId())) throw new ConflictException("Employee already has an active contract");
        Contract c = new Contract(); c.setEmployee(e); c.setType(r.type()); c.setStartDate(r.startDate()); c.setEndDate(r.endDate());
        return mapper.toResponse(contracts.saveAndFlush(c));
    }
    @Transactional
    public ContractResponse terminate(Long id, TerminationRequest r) {
        Contract c = require(id);
        lockEmployee(c.getEmployee().getId());
        if (!c.isActive()) throw new ConflictException("Contract is already terminated");
        if (r.terminationDate().isBefore(c.getStartDate()) || (c.getEndDate() != null && r.terminationDate().isAfter(c.getEndDate())))
            throw new BadRequestException("terminationDate must be within the contract dates");
        c.setTerminationDate(r.terminationDate()); c.setActive(false);
        return mapper.toResponse(contracts.saveAndFlush(c));
    }
}
