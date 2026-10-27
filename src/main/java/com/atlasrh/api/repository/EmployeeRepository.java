package com.atlasrh.api.repository;

import com.atlasrh.api.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByMatriculeIgnoreCase(String matricule);

    boolean existsByEmailIgnoreCase(String email);

    List<Employee> findByManagerId(Long managerId);

    Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
}