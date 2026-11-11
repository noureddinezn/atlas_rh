package com.atlasrh.api.service.impl;

import com.atlasrh.api.dto.request.ContractRequestDTO;
import com.atlasrh.api.dto.response.ContractResponseDTO;
import com.atlasrh.api.entity.Contract;
import com.atlasrh.api.entity.Employee;
import com.atlasrh.api.entity.enums.ContractType;
import com.atlasrh.api.exception.ResourceNotFoundException;
import com.atlasrh.api.mapper.ContractMapper;
import com.atlasrh.api.repository.ContractRepository;
import com.atlasrh.api.repository.EmployeeRepository;
import com.atlasrh.api.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional
    public ContractResponseDTO createContract(ContractRequestDTO requestDto) {
        log.info("Creating new contract for employee ID: {}", requestDto.employeeId());

        Employee employee = employeeRepository.findById(requestDto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + requestDto.employeeId()));

        if (contractRepository.existsByEmployeeIdAndActiveTrue(requestDto.employeeId())) {
            throw new IllegalStateException("Employee already has an active contract in progress");
        }

        boolean needsEndDate = requestDto.type() == ContractType.CDD || requestDto.type() == ContractType.STAGE;
        if (needsEndDate && requestDto.endDate() == null) {
            throw new IllegalArgumentException("End date is mandatory for CDD and STAGE contracts");
        }

        if (requestDto.endDate() != null && requestDto.endDate().isBefore(requestDto.startDate())) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }

        Contract contract = contractMapper.toEntity(requestDto);
        contract.setEmployee(employee);
        contract.setActive(true);

        Contract savedContract = contractRepository.save(contract);
        return contractMapper.toResponseDto(savedContract);
    }

    @Override
    @Transactional
    public ContractResponseDTO terminateContract(Long contractId) {
        log.info("Terminating contract ID: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        if (contract.getActive() == null || contract.getActive() == false) {
            throw new IllegalStateException("Contract is already terminated");
        }
        LocalDate today = LocalDate.now();
        if (contract.getStartDate().isAfter(today)) {
            throw new IllegalArgumentException("Cannot terminate a contract before its start date");
        }
        contract.setActive(false);
        contract.setEndDate(today);

        Contract updatedContract = contractRepository.save(contract);
        return contractMapper.toResponseDto(updatedContract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponseDTO> getEmployeeContractHistory(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        List<Contract> contracts = contractRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
        return contracts.stream()
                .map(contract -> contractMapper.toResponseDto(contract))
                .toList();
    }

}
