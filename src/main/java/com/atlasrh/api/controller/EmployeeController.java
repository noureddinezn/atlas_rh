package com.atlasrh.api.controller;

import com.atlasrh.api.dto.request.EmployeeRequestDTO;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;


    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(
            @Valid @RequestBody EmployeeRequestDTO dto) {

        EmployeeResponseDTO employee = service.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }


    @PutMapping("/{id}")
    public EmployeeResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto) {

        return service.updateEmployee(id, dto);
    }


    @GetMapping("/{id}")
    public EmployeeResponseDTO get(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }


    @GetMapping
    public Page<EmployeeResponseDTO> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @PageableDefault(sort = "id") Pageable pageable) {

        Set<String> allowedSortFields = Set.of(
                "id", "matricule", "firstName", "lastName",
                "email", "position", "hireDate"
        );


        for (Sort.Order order : pageable.getSort()) {
            String field = order.getProperty();

            if (!allowedSortFields.contains(field)) {
                throw new IllegalArgumentException("Unsupported sort field");
            }
        }

        return service.getAllEmployees(name, departmentId, pageable);
    }


    @GetMapping("/manager/{managerId}/team")
    public List<EmployeeResponseDTO> team(@PathVariable Long managerId) {
        return service.getManagerTeam(managerId);
    }
}
