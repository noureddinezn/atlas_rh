package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessRulesTest {
    private final EmployeeRepository employees = mock(EmployeeRepository.class);
    private final ContractRepository contracts = mock(ContractRepository.class);
    private final DepartmentRepository departments = mock(DepartmentRepository.class);
    private final DepartmentServiceImpl departmentService = new DepartmentServiceImpl(departments, employees, mock(DepartmentMapper.class));
    private final EmployeeServiceImpl employeeService = new EmployeeServiceImpl(employees, contracts, departmentService, mock(EmployeeMapper.class));
    private final ContractMapper mapper = mock(ContractMapper.class);
    private final ContractServiceImpl contractService = new ContractServiceImpl(contracts, employees, mapper);
    private final LocalDate start = LocalDate.of(2026, 1, 1);

    @Test void duplicateDepartmentIsConflict() {
        when(departments.existsByNameIgnoreCase("HR")).thenReturn(true);
        assertThrows(ConflictException.class, () -> departmentService.create(new DepartmentRequest(" HR ", null)));
    }
    @Test void occupiedDepartmentCannotBeDeleted() {
        when(departments.findById(1L)).thenReturn(Optional.of(new Department()));
        when(employees.existsByDepartmentId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> departmentService.delete(1L));
        verify(departments, never()).delete(any());
    }
    @Test void selfManagementIsBadRequest() {
        Employee e = new Employee(); e.setId(1L);
        when(employees.findById(1L)).thenReturn(Optional.of(e));
        assertThrows(BadRequestException.class, () -> employeeService.update(1L, new EmployeeRequest("M1", "a@example.com", "A", "B", 1L, 1L)));
    }
    @Test void duplicateEmailIsConflict() {
        when(employees.existsByEmailIgnoreCase("a@example.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> employeeService.create(new EmployeeRequest("M1", "a@example.com", "A", "B", 1L, null)));
    }
    @Test void fixedTermContractRequiresEndDate() {
        for (ContractType type : new ContractType[]{ContractType.CDD, ContractType.STAGE})
            assertThrows(BadRequestException.class, () -> contractService.create(new ContractRequest(1L, type, start, null)));
        verifyNoInteractions(contracts);
    }
    @Test void reversedDatesAreRejected() {
        assertThrows(BadRequestException.class, () -> contractService.create(new ContractRequest(1L, ContractType.CDI, start, start.minusDays(1))));
    }
    @Test void secondActiveContractIsConflict() {
        Employee e = new Employee(); e.setId(1L);
        when(employees.findLockedById(1L)).thenReturn(Optional.of(e));
        when(contracts.existsByEmployeeIdAndActiveTrue(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> contractService.create(new ContractRequest(1L, ContractType.CDI, start, null)));
        verify(contracts, never()).saveAndFlush(any());
    }
    @Test void terminationPreservesOriginalEndDate() {
        Employee e = new Employee(); e.setId(1L);
        Contract c = new Contract(); c.setEmployee(e); c.setStartDate(start); c.setEndDate(start.plusMonths(6));
        when(contracts.findById(1L)).thenReturn(Optional.of(c));
        when(employees.findLockedById(1L)).thenReturn(Optional.of(e));
        when(contracts.saveAndFlush(c)).thenReturn(c);
        contractService.terminate(1L, new TerminationRequest(start.plusMonths(2)));
        assertFalse(c.isActive());
        assertEquals(start.plusMonths(6), c.getEndDate());
        assertEquals(start.plusMonths(2), c.getTerminationDate());
    }
    @Test void missingEmployeeIsNotFound() {
        when(employees.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> employeeService.get(99L));
    }
}
