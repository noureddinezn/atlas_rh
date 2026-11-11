package com.atlasrh.api.service.impl;

import java.time.*;
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
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @Slf4j @RequiredArgsConstructor @Transactional(readOnly=true)
public class LeaveServiceImpl implements LeaveService {
    private final EmployeeRepository employees;
    private final LeaveTypeRepository types;
    private final LeaveRequestRepository requests;
    private final LeaveRequestMapper mapper;
    private static final Set<RequestStatus> RESERVED = Set.of(RequestStatus.EN_ATTENTE, RequestStatus.APPROUVEE);
    private ListQuerydslPredicateExecutor<Object> employeeRepository;

    private Employee getEmployee(Long id) {
        return employees.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'ID : " + id));
    }
    @Override @Transactional
    public LeaveRequestResponseDTO create(LeaveRequestCreateDTO dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("La date de fin ne peut pas précéder la date de début");
        }

        Employee employee = getEmployee(dto.employeeId());
        LeaveType type = types.findById(dto.leaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé introuvable"));

        LeaveRequest request = mapper.toEntity(dto);
        request.setEmployee(employee);
        request.setLeaveType(type);
        request.setStatus(RequestStatus.EN_ATTENTE);


        log.info("Création d'une demande de congé pour l'employé {}", employee.getId());
        LeaveRequest savedRequest = requests.save(request);
        return mapper.toResponseDto(savedRequest);
    }
    @Override
    public List<LeaveRequestResponseDTO> list(Long employeeId, RequestStatus status) {
        getEmployee(employeeId);
        return (status == null ? requests.findByEmployeeIdOrderByStartDateDesc(employeeId) : requests.findByEmployeeIdAndStatusOrderByStartDateDesc(employeeId,status))
            .stream().map(mapper::toResponseDto).toList();
    }
    @Override
    @Transactional
    public LeaveRequestResponseDTO cancel(Long requestId, Long employeeId) {

        getEmployee(employeeId);
        LeaveRequest request = requests.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));


        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Request does not belong to this employee");
        }


        if (request.getStatus() != RequestStatus.EN_ATTENTE) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }


        request.setStatus(RequestStatus.ANNULEE);
        log.info("Cancelling leave request {}", requestId);
        LeaveRequest savedRequest = requests.saveAndFlush(request);
        return mapper.toResponseDto(savedRequest);
    }

    private int calculateAnnualDays(LocalDate hireDate, LocalDate today, int baseDays) {
        int yearsWorked = Period.between(hireDate, today).getYears();
        int extraDays = Math.max(0, yearsWorked) / 5;
        return baseDays + extraDays;
    }

    @Override
    public LeaveBalanceDTO balance(Long employeeId) {

        Employee employee = getEmployee(employeeId);
        LocalDate today = LocalDate.now();
        int year = today.getYear();


        List<LeaveRequest> employeeRequests = requests.findByEmployeeIdAndStatusIn(employeeId, RESERVED);
        List<LeaveType> leaveTypes = types.findAll();
        List<LeaveBalanceDTO.TypeBalance> balances = new ArrayList<>();


        for (LeaveType type : leaveTypes) {
            int daysPerYear = calculateAnnualDays(employee.getHireDate(), today, type.getDaysPerYear());
            long usedDays = 0;

            for (LeaveRequest request : employeeRequests) {
                if (request.getLeaveType().getId().equals(type.getId())) {

                    long requestDays = WorkingDays.inYear(
                            request.getStartDate(), request.getEndDate(), year);
                    usedDays = usedDays + requestDays;
                }
            }


            long remainingDays = daysPerYear - usedDays;

            LeaveBalanceDTO.TypeBalance typeBalance = new LeaveBalanceDTO.TypeBalance(
                    type.getId(), type.getName(), daysPerYear, usedDays, remainingDays);
            balances.add(typeBalance);
        }


        Period seniority = Period.between(employee.getHireDate(), today);
        return new LeaveBalanceDTO(employeeId, year, seniority, balances);
    }

//    public List<Employee> getEmployee(){
//        return employyeRepository.findAll().stream().toList();
//    }

//    public List<Employee> getAhmedEmployee(){
//        return EmployeeRepository.findAll().stream()
//                .filter(e -> getFirstName().equalsIgnoreCase("AHmed"));
//    }

//    public List<Employee>getEmployeeByDepartment(Long departmentIt){
////        return employeeRepository.findAll().stream()
////                .filter(e -> e.getDepartment() != null)
////                .filter(e -> e.getDepartment().getId().equals(departmentIt))
////                .toList();
////    }


}
