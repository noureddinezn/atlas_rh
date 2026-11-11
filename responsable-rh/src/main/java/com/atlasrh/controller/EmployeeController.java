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
@RestController @RequestMapping("/api/employees") @RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeServiceImpl service;
    private static final Set<String> SORT = Set.of("id","matricule","email","firstName","lastName");
    @GetMapping
    public PageResponse<EmployeeResponse> list(@RequestParam(required=false) String name, @RequestParam(required=false) Long departmentId, @PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.search(name, departmentId, Paging.check(p, SORT))); }
    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/employees/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest r) { return service.update(id,r); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

    @GetMapping("/{id}/team")
    public PageResponse<EmployeeResponse> team(@PathVariable Long id, @PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.team(id, Paging.check(p, SORT))); }
}
