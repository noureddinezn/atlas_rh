package com.atlasrh.repository;

import com.atlasrh.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByEmployeeIdAndActiveTrue(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    Page<Contract> findByEmployeeId(Long employeeId, Pageable pageable);
}
