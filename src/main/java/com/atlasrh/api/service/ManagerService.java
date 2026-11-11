package com.atlasrh.api.service;

import java.util.List;
import com.atlasrh.api.dto.request.LeaveDecisionDTO;
import com.atlasrh.api.dto.response.LeaveRequestResponseDTO;
public interface ManagerService {
    List<LeaveRequestResponseDTO> pending(Long managerId);
    LeaveRequestResponseDTO decide(Long managerId, Long requestId, LeaveDecisionDTO dto);
}
