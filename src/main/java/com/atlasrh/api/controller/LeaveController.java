package com.atlasrh.api.controller;

import com.atlasrh.api.dto.request.LeaveRequestCreateDTO;
import com.atlasrh.api.dto.response.LeaveBalanceDTO;
import com.atlasrh.api.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.api.entity.enums.RequestStatus;
import com.atlasrh.api.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService service;


    @PostMapping
    public ResponseEntity<LeaveRequestResponseDTO> create(
            @Valid @RequestBody LeaveRequestCreateDTO dto) {

        LeaveRequestResponseDTO leaveRequest = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequest);
    }


    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequestResponseDTO> list(
            @PathVariable Long employeeId,
            @RequestParam(required = false) RequestStatus status) {

        return service.list(employeeId, status);
    }


    @PatchMapping("/{requestId}/cancel")
    public LeaveRequestResponseDTO cancel(
            @PathVariable Long requestId,
            @RequestParam Long employeeId) {

        return service.cancel(requestId, employeeId);
    }


    @GetMapping("/employee/{employeeId}/balance")
    public LeaveBalanceDTO balance(@PathVariable Long employeeId) {
        return service.balance(employeeId);
    }
}
