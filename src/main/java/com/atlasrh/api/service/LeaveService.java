package com.atlasrh.api.service;

import java.util.List;
import com.atlasrh.api.dto.request.*;
import com.atlasrh.api.dto.response.*;
import com.atlasrh.api.entity.enums.RequestStatus;
public interface LeaveService {
    LeaveRequestResponseDTO create(LeaveRequestCreateDTO dto);
    List<LeaveRequestResponseDTO> list(Long employeeId, RequestStatus status);
    LeaveRequestResponseDTO cancel(Long requestId, Long employeeId);
    LeaveBalanceDTO balance(Long employeeId);
}
