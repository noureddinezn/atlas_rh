package com.atlasrh.api.controller;

import com.atlasrh.api.dto.request.ContractRequestDTO;
import com.atlasrh.api.dto.response.ContractResponseDTO;
import com.atlasrh.api.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    // Ajouter un contrat.
    @PostMapping
    public ResponseEntity<ContractResponseDTO> create(
            @Valid @RequestBody ContractRequestDTO dto) {

        ContractResponseDTO contract = service.createContract(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(contract);
    }

    // Terminer un contrat avec son id.
    @PatchMapping("/{id}/terminate")
    public ContractResponseDTO terminate(@PathVariable Long id) {
        return service.terminateContract(id);
    }

    // Recuperer les contrats d'un employe.
    @GetMapping("/employee/{employeeId}")
    public List<ContractResponseDTO> history(@PathVariable Long employeeId) {
        return service.getEmployeeContractHistory(employeeId);
    }
}
