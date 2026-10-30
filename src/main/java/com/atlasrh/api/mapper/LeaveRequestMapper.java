package com.atlasrh.api.mapper;

import org.mapstruct.*;
import com.atlasrh.api.entity.LeaveRequest;
import com.atlasrh.api.dto.request.LeaveRequestCreateDTO;
import com.atlasrh.api.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.api.service.WorkingDays;
@Mapper(componentModel="spring", imports=WorkingDays.class, unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface LeaveRequestMapper {
    @Mapping(target="id",ignore=true)
    @Mapping(target="employee",ignore=true)
    @Mapping(target="leaveType",ignore=true)
    @Mapping(target="status",constant="EN_ATTENTE")
    @Mapping(target="comment",ignore=true)
    LeaveRequest toEntity(LeaveRequestCreateDTO dto);
    @Mapping(target="employeeId",source="employee.id")
    @Mapping(target="employeeFirstName",source="employee.firstName")
    @Mapping(target="employeeLastName",source="employee.lastName")
    @Mapping(target="leaveTypeId",source="leaveType.id")
    @Mapping(target="leaveTypeName",source="leaveType.name")
    @Mapping(target="workingDays",expression="java(WorkingDays.count(entity.getStartDate(), entity.getEndDate()))")
    LeaveRequestResponseDTO toResponseDto(LeaveRequest entity);
}
