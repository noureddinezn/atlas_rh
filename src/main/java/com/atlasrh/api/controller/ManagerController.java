package com.atlasrh.api.controller;

import com.atlasrh.api.dto.request.LeaveDecisionDTO;
import com.atlasrh.api.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.api.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService service;

    // Lister les demandes de conge en attente pour un manager.
    @GetMapping("/{managerId}/leave-requests/pending")
    public List<LeaveRequestResponseDTO> pending(@PathVariable Long managerId) {
        return service.pending(managerId);
    }

    // Enregistrer la decision du manager sur une demande de conge.
    @PatchMapping("/leave-requests/{requestId}/decision")
    public LeaveRequestResponseDTO decision(
            @PathVariable Long requestId,
            @RequestParam Long managerId,
            @Valid @RequestBody LeaveDecisionDTO dto) {

        return service.decide(managerId, requestId, dto);
    }
}
