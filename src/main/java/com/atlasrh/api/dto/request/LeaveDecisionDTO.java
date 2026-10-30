package com.atlasrh.api.dto.request;

import jakarta.validation.constraints.*;
import com.atlasrh.api.entity.enums.RequestStatus;
public record LeaveDecisionDTO(@NotNull RequestStatus status, @Size(max=255) String comment) {}
