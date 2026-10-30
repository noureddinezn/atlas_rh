package com.atlasrh.api.dto.response;

import java.time.LocalDate;
import com.atlasrh.api.entity.enums.RequestStatus;
public record LeaveRequestResponseDTO(Long id, Long employeeId, String employeeFirstName, String employeeLastName,
    Long leaveTypeId, String leaveTypeName, LocalDate startDate, LocalDate endDate, String reason,
    RequestStatus status, String comment, long workingDays) {}
