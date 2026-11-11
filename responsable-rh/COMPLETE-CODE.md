# Complete Responsable RH source code

Each heading is the relative destination path. All files are also available separately.

## `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-parent</artifactId><version>3.3.13</version><relativePath/></parent>
  <groupId>com.atlasrh</groupId><artifactId>responsable-rh</artifactId><version>1.0.0</version>
  <properties><java.version>17</java.version><mapstruct.version>1.6.3</mapstruct.version></properties>
  <dependencies>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
    <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>
    <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><optional>true</optional></dependency>
    <dependency><groupId>org.mapstruct</groupId><artifactId>mapstruct</artifactId><version>${mapstruct.version}</version></dependency>
    <dependency><groupId>org.flywaydb</groupId><artifactId>flyway-core</artifactId></dependency>
    <dependency><groupId>org.flywaydb</groupId><artifactId>flyway-database-postgresql</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
  </dependencies>
  <build><plugins>
    <plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><configuration><annotationProcessorPaths>
      <path><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><version>${lombok.version}</version></path>
      <path><groupId>org.projectlombok</groupId><artifactId>lombok-mapstruct-binding</artifactId><version>0.2.0</version></path>
      <path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId><version>${mapstruct.version}</version></path>
    </annotationProcessorPaths></configuration></plugin>
    <plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId><configuration><excludes><exclude><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId></exclude></excludes></configuration></plugin>
  </plugins></build>
</project>
```

## `src/main/java/com/atlasrh/AtlasRhApplication.java`

```java
package com.atlasrh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class AtlasRhApplication {
    public static void main(String[] args) { SpringApplication.run(AtlasRhApplication.class, args); }
}
```

## `src/main/java/com/atlasrh/controller/ContractController.java`

```java
package com.atlasrh.controller;

import java.net.URI;
import java.util.Set;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.atlasrh.dto.*;
import com.atlasrh.service.*;
@RestController @RequestMapping("/api/contracts") @RequiredArgsConstructor
public class ContractController {
    private final ContractServiceImpl service;
    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/contracts/" + result.id())).body(result);
    }
    @GetMapping("/{id}")
    public ContractResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/employee/{employeeId}")
    public PageResponse<ContractResponse> history(@PathVariable Long employeeId, @PageableDefault(sort="id") Pageable p) {
        return PageResponse.from(service.history(employeeId, Paging.check(p, Set.of("id","startDate","endDate","type","active"))));
    }
    @PatchMapping("/{id}/terminate")
    public ContractResponse terminate(@PathVariable Long id, @Valid @RequestBody TerminationRequest r) { return service.terminate(id,r); }
}
```

## `src/main/java/com/atlasrh/controller/DepartmentController.java`

```java
package com.atlasrh.controller;

import java.net.URI;
import java.util.Set;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.atlasrh.dto.*;
import com.atlasrh.service.*;
@RestController @RequestMapping("/api/departments") @RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentServiceImpl service;
    private static final Set<String> SORT = Set.of("id","name");
    @GetMapping
    public PageResponse<DepartmentResponse> list(@PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.list(Paging.check(p, SORT))); }
    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/departments/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest r) { return service.update(id,r); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
```

## `src/main/java/com/atlasrh/controller/EmployeeController.java`

```java
package com.atlasrh.controller;

import java.net.URI;
import java.util.Set;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.atlasrh.dto.*;
import com.atlasrh.service.*;
@RestController @RequestMapping("/api/employees") @RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeServiceImpl service;
    private static final Set<String> SORT = Set.of("id","matricule","email","firstName","lastName");
    @GetMapping
    public PageResponse<EmployeeResponse> list(@RequestParam(required=false) String name, @RequestParam(required=false) Long departmentId, @PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.search(name, departmentId, Paging.check(p, SORT))); }
    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest r) {
        var result = service.create(r); return ResponseEntity.created(URI.create("/api/employees/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest r) { return service.update(id,r); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

    @GetMapping("/{id}/team")
    public PageResponse<EmployeeResponse> team(@PathVariable Long id, @PageableDefault(sort="id") Pageable p) { return PageResponse.from(service.team(id, Paging.check(p, SORT))); }
}
```

## `src/main/java/com/atlasrh/controller/Paging.java`

```java
package com.atlasrh.controller;

import java.util.Set;
import org.springframework.data.domain.Pageable;
import com.atlasrh.exception.BadRequestException;
final class Paging {
    private Paging() {}
    static Pageable check(Pageable p, Set<String> allowed) {
        for (var order : p.getSort()) if (!allowed.contains(order.getProperty())) throw new BadRequestException("Unsupported sort field: " + order.getProperty());
        return p;
    }
}
```

## `src/main/java/com/atlasrh/dto/ContractRequest.java`

```java
package com.atlasrh.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import com.atlasrh.entity.ContractType;
public record ContractRequest(@NotNull @Positive Long employeeId, @NotNull ContractType type,
    @NotNull LocalDate startDate, LocalDate endDate) {}
```

## `src/main/java/com/atlasrh/dto/ContractResponse.java`

```java
package com.atlasrh.dto;

import java.time.LocalDate;
import com.atlasrh.entity.ContractType;
public record ContractResponse(Long id, Long employeeId, ContractType type, LocalDate startDate,
    LocalDate endDate, LocalDate terminationDate, boolean active) {}
```

## `src/main/java/com/atlasrh/dto/DepartmentRequest.java`

```java
package com.atlasrh.dto;

import jakarta.validation.constraints.*;
public record DepartmentRequest(@NotBlank @Size(max=100) String name, @Size(max=500) String description) {}
```

## `src/main/java/com/atlasrh/dto/DepartmentResponse.java`

```java
package com.atlasrh.dto;

public record DepartmentResponse(Long id, String name, String description) {}
```

## `src/main/java/com/atlasrh/dto/EmployeeRequest.java`

```java
package com.atlasrh.dto;

import jakarta.validation.constraints.*;
public record EmployeeRequest(
    @NotBlank @Size(max=50) String matricule,
    @NotBlank @Email @Size(max=254) String email,
    @NotBlank @Size(max=100) String firstName,
    @NotBlank @Size(max=100) String lastName,
    @NotNull @Positive Long departmentId, @Positive Long managerId) {}
```

## `src/main/java/com/atlasrh/dto/EmployeeResponse.java`

```java
package com.atlasrh.dto;

public record EmployeeResponse(Long id, String matricule, String email, String firstName, String lastName, Long departmentId, String departmentName, Long managerId) {}
```

## `src/main/java/com/atlasrh/dto/PageResponse.java`

```java
package com.atlasrh.dto;

import java.util.List;
import org.springframework.data.domain.Page;
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public static <T> PageResponse<T> from(Page<T> p) { return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()); }
}
```

## `src/main/java/com/atlasrh/dto/TerminationRequest.java`

```java
package com.atlasrh.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
public record TerminationRequest(@NotNull LocalDate terminationDate) {}
```

## `src/main/java/com/atlasrh/entity/Contract.java`

```java
package com.atlasrh.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name = "contracts")
public class Contract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employee_id", nullable = false) private Employee employee;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private ContractType type;
    @Column(nullable = false) private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate terminationDate;
    @Column(nullable = false) private boolean active = true;
    @Version private long version;
}
```

## `src/main/java/com/atlasrh/entity/ContractType.java`

```java
package com.atlasrh.entity;

public enum ContractType { CDI, CDD, STAGE }
```

## `src/main/java/com/atlasrh/entity/Department.java`

```java
package com.atlasrh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name = "departments")
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 500) private String description;
    @Version private long version;
}
```

## `src/main/java/com/atlasrh/entity/Employee.java`

```java
package com.atlasrh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String matricule;
    @Column(nullable = false, length = 254) private String email;
    @Column(nullable = false, length = 100) private String firstName;
    @Column(nullable = false, length = 100) private String lastName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "department_id", nullable = false) private Department department;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manager_id") private Employee manager;
    @Version private long version;
}
```

## `src/main/java/com/atlasrh/exception/BadRequestException.java`

```java
package com.atlasrh.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
```

## `src/main/java/com/atlasrh/exception/ConflictException.java`

```java
package com.atlasrh.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
```

## `src/main/java/com/atlasrh/exception/GlobalExceptionHandler.java`

```java
package com.atlasrh.exception;

import java.time.Instant;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(Instant timestamp, int status, String error, String message, String path, Map<String, String> fields) {}
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fields));
    }
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> missing(NotFoundException e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiError> conflict(ConflictException e, HttpServletRequest r) { return error(HttpStatus.CONFLICT, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ApiError> bad(BadRequestException e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST, e.getMessage(), r, Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String,String> fields = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", r, fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, org.springframework.web.bind.MissingServletRequestParameterException.class})
    ResponseEntity<ApiError> malformed(Exception e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST, "Invalid request, date, or enum value", r, Map.of()); }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    ResponseEntity<ApiError> integrity(Exception e, HttpServletRequest r) { return error(HttpStatus.CONFLICT, "Conflicting data or concurrent modification; reload and retry", r, Map.of()); }
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiError> route(Exception e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND, "Endpoint not found", r, Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiError> method(Exception e, HttpServletRequest r) { return error(HttpStatus.METHOD_NOT_ALLOWED, "Method not supported", r, Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiError> media(Exception e, HttpServletRequest r) { return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Use application/json", r, Map.of()); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception e, HttpServletRequest r) {
        log.error("Unexpected request failure", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", r, Map.of());
    }
}
```

## `src/main/java/com/atlasrh/exception/NotFoundException.java`

```java
package com.atlasrh.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
```

## `src/main/java/com/atlasrh/mapper/ContractMapper.java`

```java
package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Contract;
import com.atlasrh.dto.ContractResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface ContractMapper {
    @Mapping(target="employeeId", source="employee.id")
    ContractResponse toResponse(Contract entity);
}
```

## `src/main/java/com/atlasrh/mapper/DepartmentMapper.java`

```java
package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Department;
import com.atlasrh.dto.DepartmentResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface DepartmentMapper {
    DepartmentResponse toResponse(Department entity);
}
```

## `src/main/java/com/atlasrh/mapper/EmployeeMapper.java`

```java
package com.atlasrh.mapper;

import org.mapstruct.*;
import com.atlasrh.entity.Employee;
import com.atlasrh.dto.EmployeeResponse;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface EmployeeMapper {
    @Mapping(target="departmentId", source="department.id")
    @Mapping(target="departmentName", source="department.name")
    @Mapping(target="managerId", source="manager.id")
    EmployeeResponse toResponse(Employee entity);
}
```

## `src/main/java/com/atlasrh/repository/ContractRepository.java`

```java
package com.atlasrh.repository;

import com.atlasrh.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByEmployeeIdAndActiveTrue(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    Page<Contract> findByEmployeeId(Long employeeId, Pageable pageable);
}
```

## `src/main/java/com/atlasrh/repository/DepartmentRepository.java`

```java
package com.atlasrh.repository;

import com.atlasrh.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
```

## `src/main/java/com/atlasrh/repository/EmployeeRepository.java`

```java
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
```

## `src/main/java/com/atlasrh/service/ContractServiceImpl.java`

```java
package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ContractServiceImpl {
    private final ContractRepository contracts;
    private final EmployeeRepository employees;
    private final ContractMapper mapper;
    private Contract require(Long id) { return contracts.findById(id).orElseThrow(() -> new NotFoundException("Contract not found: " + id)); }
    private Employee lockEmployee(Long id) { return employees.findLockedById(id).orElseThrow(() -> new NotFoundException("Employee not found: " + id)); }
    public ContractResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<ContractResponse> history(Long employeeId, Pageable p) {
        if (!employees.existsById(employeeId)) throw new NotFoundException("Employee not found: " + employeeId);
        return contracts.findByEmployeeId(employeeId, p).map(mapper::toResponse);
    }
    @Transactional
    public ContractResponse create(ContractRequest r) {
        if (r.type() != ContractType.CDI && r.endDate() == null) throw new BadRequestException("CDD and STAGE require endDate");
        if (r.endDate() != null && r.endDate().isBefore(r.startDate())) throw new BadRequestException("endDate must be on or after startDate");
        Employee e = lockEmployee(r.employeeId());
        if (contracts.existsByEmployeeIdAndActiveTrue(e.getId())) throw new ConflictException("Employee already has an active contract");
        Contract c = new Contract(); c.setEmployee(e); c.setType(r.type()); c.setStartDate(r.startDate()); c.setEndDate(r.endDate());
        return mapper.toResponse(contracts.saveAndFlush(c));
    }
    @Transactional
    public ContractResponse terminate(Long id, TerminationRequest r) {
        Contract c = require(id);
        lockEmployee(c.getEmployee().getId());
        if (!c.isActive()) throw new ConflictException("Contract is already terminated");
        if (r.terminationDate().isBefore(c.getStartDate()) || (c.getEndDate() != null && r.terminationDate().isAfter(c.getEndDate())))
            throw new BadRequestException("terminationDate must be within the contract dates");
        c.setTerminationDate(r.terminationDate()); c.setActive(false);
        return mapper.toResponse(contracts.saveAndFlush(c));
    }
}
```

## `src/main/java/com/atlasrh/service/DepartmentServiceImpl.java`

```java
package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class DepartmentServiceImpl {
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    private final DepartmentMapper mapper;
    public Department require(Long id) { return departments.findById(id).orElseThrow(() -> new NotFoundException("Department not found: " + id)); }
    public DepartmentResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<DepartmentResponse> list(Pageable p) { return departments.findAll(p).map(mapper::toResponse); }
    @Transactional
    public DepartmentResponse create(DepartmentRequest r) { return save(new Department(), r); }
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest r) { return save(require(id), r); }
    private DepartmentResponse save(Department d, DepartmentRequest r) {
        String name = r.name().strip();
        if (d.getId() == null ? departments.existsByNameIgnoreCase(name) : departments.existsByNameIgnoreCaseAndIdNot(name, d.getId()))
            throw new ConflictException("Department name already exists");
        d.setName(name); d.setDescription(r.description());
        return mapper.toResponse(departments.saveAndFlush(d));
    }
    @Transactional
    public void delete(Long id) {
        Department d = require(id);
        if (employees.existsByDepartmentId(id)) throw new ConflictException("Department contains employees");
        departments.delete(d); departments.flush();
    }
}
```

## `src/main/java/com/atlasrh/service/EmployeeServiceImpl.java`

```java
package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EmployeeServiceImpl {
    private final EmployeeRepository employees;
    private final ContractRepository contracts;
    private final DepartmentServiceImpl departments;
    private final EmployeeMapper mapper;
    public Employee require(Long id) { return employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found: " + id)); }
    public EmployeeResponse get(Long id) { return mapper.toResponse(require(id)); }
    public Page<EmployeeResponse> search(String name, Long departmentId, Pageable p) {
        Specification<Employee> spec = (r,q,b) -> b.conjunction();
        if (name != null && !name.isBlank()) {
            String value = "%" + name.strip().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
            spec = spec.and((r,q,b) -> b.like(b.lower(b.concat(b.concat(r.get("firstName"), " "), r.get("lastName"))), value, '!'));
        }
        if (departmentId != null) { departments.require(departmentId); spec = spec.and((r,q,b) -> b.equal(r.get("department").get("id"), departmentId)); }
        return employees.findAll(spec, p).map(mapper::toResponse);
    }
    public Page<EmployeeResponse> team(Long id, Pageable p) { require(id); return employees.findByManagerId(id, p).map(mapper::toResponse); }
    @Transactional
    public EmployeeResponse create(EmployeeRequest r) { return save(new Employee(), r); }
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest r) { return save(require(id), r); }
    private EmployeeResponse save(Employee e, EmployeeRequest r) {
        String matricule = r.matricule().strip(); String email = r.email().strip().toLowerCase(Locale.ROOT);
        Long id = e.getId();
        if (id == null ? employees.existsByMatriculeIgnoreCase(matricule) : employees.existsByMatriculeIgnoreCaseAndIdNot(matricule,id)) throw new ConflictException("Matricule already exists");
        if (id == null ? employees.existsByEmailIgnoreCase(email) : employees.existsByEmailIgnoreCaseAndIdNot(email,id)) throw new ConflictException("Email already exists");
        if (id != null && id.equals(r.managerId())) throw new BadRequestException("Employee cannot manage themselves");
        e.setDepartment(departments.require(r.departmentId()));
        e.setManager(r.managerId() == null ? null : require(r.managerId()));
        e.setMatricule(matricule); e.setEmail(email); e.setFirstName(r.firstName().strip()); e.setLastName(r.lastName().strip());
        return mapper.toResponse(employees.saveAndFlush(e));
    }
    @Transactional
    public void delete(Long id) {
        Employee e = require(id);
        if (employees.existsByManagerId(id)) throw new ConflictException("Employee manages a team");
        if (contracts.existsByEmployeeId(id)) throw new ConflictException("Employee has contract history");
        employees.delete(e); employees.flush();
    }
}
```

## `src/test/java/com/atlasrh/service/BusinessRulesTest.java`

```java
package com.atlasrh.service;

import com.atlasrh.dto.*;
import com.atlasrh.entity.*;
import com.atlasrh.exception.*;
import com.atlasrh.mapper.*;
import com.atlasrh.repository.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessRulesTest {
    private final EmployeeRepository employees = mock(EmployeeRepository.class);
    private final ContractRepository contracts = mock(ContractRepository.class);
    private final DepartmentRepository departments = mock(DepartmentRepository.class);
    private final DepartmentServiceImpl departmentService = new DepartmentServiceImpl(departments, employees, mock(DepartmentMapper.class));
    private final EmployeeServiceImpl employeeService = new EmployeeServiceImpl(employees, contracts, departmentService, mock(EmployeeMapper.class));
    private final ContractMapper mapper = mock(ContractMapper.class);
    private final ContractServiceImpl contractService = new ContractServiceImpl(contracts, employees, mapper);
    private final LocalDate start = LocalDate.of(2026, 1, 1);

    @Test void duplicateDepartmentIsConflict() {
        when(departments.existsByNameIgnoreCase("HR")).thenReturn(true);
        assertThrows(ConflictException.class, () -> departmentService.create(new DepartmentRequest(" HR ", null)));
    }
    @Test void occupiedDepartmentCannotBeDeleted() {
        when(departments.findById(1L)).thenReturn(Optional.of(new Department()));
        when(employees.existsByDepartmentId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> departmentService.delete(1L));
        verify(departments, never()).delete(any());
    }
    @Test void selfManagementIsBadRequest() {
        Employee e = new Employee(); e.setId(1L);
        when(employees.findById(1L)).thenReturn(Optional.of(e));
        assertThrows(BadRequestException.class, () -> employeeService.update(1L, new EmployeeRequest("M1", "a@example.com", "A", "B", 1L, 1L)));
    }
    @Test void duplicateEmailIsConflict() {
        when(employees.existsByEmailIgnoreCase("a@example.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> employeeService.create(new EmployeeRequest("M1", "a@example.com", "A", "B", 1L, null)));
    }
    @Test void fixedTermContractRequiresEndDate() {
        for (ContractType type : new ContractType[]{ContractType.CDD, ContractType.STAGE})
            assertThrows(BadRequestException.class, () -> contractService.create(new ContractRequest(1L, type, start, null)));
        verifyNoInteractions(contracts);
    }
    @Test void reversedDatesAreRejected() {
        assertThrows(BadRequestException.class, () -> contractService.create(new ContractRequest(1L, ContractType.CDI, start, start.minusDays(1))));
    }
    @Test void secondActiveContractIsConflict() {
        Employee e = new Employee(); e.setId(1L);
        when(employees.findLockedById(1L)).thenReturn(Optional.of(e));
        when(contracts.existsByEmployeeIdAndActiveTrue(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> contractService.create(new ContractRequest(1L, ContractType.CDI, start, null)));
        verify(contracts, never()).saveAndFlush(any());
    }
    @Test void terminationPreservesOriginalEndDate() {
        Employee e = new Employee(); e.setId(1L);
        Contract c = new Contract(); c.setEmployee(e); c.setStartDate(start); c.setEndDate(start.plusMonths(6));
        when(contracts.findById(1L)).thenReturn(Optional.of(c));
        when(employees.findLockedById(1L)).thenReturn(Optional.of(e));
        when(contracts.saveAndFlush(c)).thenReturn(c);
        contractService.terminate(1L, new TerminationRequest(start.plusMonths(2)));
        assertFalse(c.isActive());
        assertEquals(start.plusMonths(6), c.getEndDate());
        assertEquals(start.plusMonths(2), c.getTerminationDate());
    }
    @Test void missingEmployeeIsNotFound() {
        when(employees.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> employeeService.get(99L));
    }
}
```

## `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: responsable-rh
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/atlas_rh}
    username: ${DB_USERNAME:atlas}
    password: ${DB_PASSWORD}
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
  jackson:
    deserialization:
      fail-on-unknown-properties: true
  data:
    web:
      pageable:
        max-page-size: 100
server:
  port: ${PORT:8080}
  error:
    include-message: never
    include-stacktrace: never
```

## `src/main/resources/db/migration/V1__rh_schema.sql`

```sql
CREATE TABLE departments (
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 name VARCHAR(100) NOT NULL CHECK (length(trim(name)) > 0), description VARCHAR(500), version BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uq_department_name ON departments (lower(name));
CREATE TABLE employees (
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 matricule VARCHAR(50) NOT NULL, email VARCHAR(254) NOT NULL,
 first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL,
 department_id BIGINT NOT NULL REFERENCES departments(id), manager_id BIGINT REFERENCES employees(id),
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_employee_manager CHECK (manager_id <> id)
);
CREATE UNIQUE INDEX uq_employee_matricule ON employees(lower(matricule));
CREATE UNIQUE INDEX uq_employee_email ON employees(lower(email));
CREATE INDEX ix_employee_department ON employees(department_id);
CREATE INDEX ix_employee_manager ON employees(manager_id);
CREATE TABLE contracts (
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 employee_id BIGINT NOT NULL REFERENCES employees(id), type VARCHAR(10) NOT NULL,
 start_date DATE NOT NULL, end_date DATE, termination_date DATE,
 active BOOLEAN NOT NULL DEFAULT true, version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT ck_contract_type CHECK (type IN ('CDI','CDD','STAGE')),
 CONSTRAINT ck_contract_dates CHECK ((type = 'CDI' OR end_date IS NOT NULL) AND (end_date IS NULL OR end_date >= start_date)),
 CONSTRAINT ck_contract_termination CHECK ((active AND termination_date IS NULL) OR
   (NOT active AND termination_date IS NOT NULL AND termination_date >= start_date AND (end_date IS NULL OR termination_date <= end_date)))
);
CREATE UNIQUE INDEX uq_one_active_contract ON contracts(employee_id) WHERE active;
CREATE INDEX ix_contract_employee ON contracts(employee_id);
```

## `compose.yaml`

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: atlas_rh
      POSTGRES_USER: atlas
      POSTGRES_PASSWORD: ${DB_PASSWORD:?Set DB_PASSWORD}
    ports:
      - "127.0.0.1:5432:5432"
    volumes:
      - rh_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U atlas -d atlas_rh"]
      interval: 5s
      timeout: 5s
      retries: 10
volumes:
  rh_data:
```
