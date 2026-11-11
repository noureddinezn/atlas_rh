package com.atlasrh.api.repository;

import java.util.List;
import java.util.Collection;
import com.atlasrh.api.entity.LeaveRequest;
import com.atlasrh.api.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {
    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
    List<LeaveRequest> findByEmployeeIdAndStatusOrderByStartDateDesc(Long employeeId, RequestStatus status);
    List<LeaveRequest> findByEmployeeIdAndStatusIn(Long employeeId, Collection<RequestStatus> statuses);
    List<LeaveRequest> findByEmployeeManagerIdAndStatus(Long managerId, RequestStatus status);
}
