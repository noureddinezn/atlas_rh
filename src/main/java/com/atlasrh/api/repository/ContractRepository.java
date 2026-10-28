package com.atlasrh.api.repository;

import com.atlasrh.api.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    boolean existsByEmployeeIdAndActiveTrue(Long employeeId);

    List<Contract> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
}