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
