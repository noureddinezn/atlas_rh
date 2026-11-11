package com.atlasrh.controller;

import java.net.URI;
import java.util.Set;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.atlasrh.dto.*;
import com.atlasrh.service.*;
@RestController @RequestMapping("/api/contracts") @RequiredArgsConstructor
public class ContractController {
    private final ContractServiceImpl service;
    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/contracts/" + result.id())).body(result);
    }
    @GetMapping("/{id}")
    public ContractResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/employee/{employeeId}")
    public PageResponse<ContractResponse> history(@PathVariable Long employeeId, @PageableDefault(sort="id") Pageable p) {
        return PageResponse.from(service.history(employeeId, Paging.check(p, Set.of("id","startDate","endDate","type","active"))));
    }
    @PatchMapping("/{id}/terminate")
    public ContractResponse terminate(@PathVariable Long id, @Valid @RequestBody TerminationRequest r) { return service.terminate(id,r); }
}
