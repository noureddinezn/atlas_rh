package com.atlasrh.api.dto.response;

import java.util.List;
import java.time.Period;
public record LeaveBalanceDTO(Long employeeId, int year, Period seniority, List<TypeBalance> balances) {
    public record TypeBalance(Long leaveTypeId, String name, int daysPerYear, long usedDays, long remainingDays) {}
}
