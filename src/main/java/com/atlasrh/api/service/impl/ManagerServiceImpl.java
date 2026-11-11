package com.atlasrh.api.service.impl;

import java.util.*;
import com.atlasrh.api.dto.request.*;
import com.atlasrh.api.dto.response.*;
import com.atlasrh.api.entity.*;
import com.atlasrh.api.entity.enums.RequestStatus;
import com.atlasrh.api.exception.*;
import com.atlasrh.api.repository.*;
import com.atlasrh.api.mapper.LeaveRequestMapper;
import com.atlasrh.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerServiceImpl implements ManagerService {
    private final EmployeeRepository employees;
    private final LeaveRequestRepository requests;
    private final LeaveRequestMapper mapper;
    @Override
    public List<LeaveRequestResponseDTO> pending(Long managerId) {

        if (!employees.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager not found");
        }


        List<LeaveRequest> pendingRequests = requests.findByEmployeeManagerIdAndStatus(
                managerId, RequestStatus.EN_ATTENTE);
        List<LeaveRequestResponseDTO> result = new ArrayList<>();
        for (LeaveRequest request : pendingRequests) {
            result.add(mapper.toResponseDto(request));
        }
        return result;
    }
    @Override
    @Transactional
    public LeaveRequestResponseDTO decide(Long managerId, Long requestId, LeaveDecisionDTO dto) {

        if (dto.status() != RequestStatus.APPROUVEE && dto.status() != RequestStatus.REFUSEE) {
            throw new IllegalArgumentException("Decision must be APPROUVEE or REFUSEE");
        }
        if (dto.status() == RequestStatus.REFUSEE && (dto.comment() == null || dto.comment().isBlank())) {
            throw new IllegalArgumentException("A refusal requires a comment");
        }


        if (!employees.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager not found");
        }
        LeaveRequest request = requests.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        Employee manager = request.getEmployee().getManager();
        if (manager == null || !manager.getId().equals(managerId)) {
            throw new IllegalArgumentException("Request does not belong to this manager's team");
        }


        if (request.getStatus() != RequestStatus.EN_ATTENTE) {
            throw new IllegalStateException("Only pending requests can be decided");
        }


        request.setStatus(dto.status());
        String comment = dto.comment();
        if (comment != null) {
            comment = comment.strip();
        }
        request.setComment(comment);
        log.info("Decision {} for leave request {}", dto.status(), requestId);
        LeaveRequest savedRequest = requests.save(request);
        return mapper.toResponseDto(savedRequest);
    }
}
