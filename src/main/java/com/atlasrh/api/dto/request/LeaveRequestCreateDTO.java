package com.atlasrh.api.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
public record LeaveRequestCreateDTO(@NotNull @Positive Long employeeId, @NotNull @Positive Long leaveTypeId,
    @NotNull LocalDate startDate, @NotNull LocalDate endDate, @NotBlank @Size(max=255) String reason) {}
