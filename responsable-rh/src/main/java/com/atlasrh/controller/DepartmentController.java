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
@RestController @RequestMapping("/api/departments") @RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentServiceImpl service;
    private static final Set<String> SORT = Set.of("id","name");
    @GetMapping
    public PageResponse<DepartmentResponse> list(@PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.list(Paging.check(p, SORT))); }
    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/departments/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest r) { return service.update(id,r); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
