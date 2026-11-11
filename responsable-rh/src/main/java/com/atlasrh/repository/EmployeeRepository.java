package com.atlasrh.repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import com.atlasrh.entity.Employee;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    boolean existsByMatriculeIgnoreCase(String matricule);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByMatriculeIgnoreCaseAndIdNot(String matricule, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByDepartmentId(Long departmentId);
    boolean existsByManagerId(Long managerId);
    Page<Employee> findByManagerId(Long managerId, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findLockedById(@Param("id") Long id);
}
