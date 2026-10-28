package com.atlasrh.api.service;

import com.atlasrh.api.dto.request.ContractRequestDTO;
import com.atlasrh.api.dto.response.ContractResponseDTO;

import java.util.List;

public interface ContractService {

    ContractResponseDTO createContract(ContractRequestDTO requestDto);

    ContractResponseDTO terminateContract(Long contractId);

    List<ContractResponseDTO> getEmployeeContractHistory(Long employeeId);
}