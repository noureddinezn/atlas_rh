package com.atlasrh.api;

import com.atlasrh.api.controller.EmployeeController;
import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeControllerTest {

    private final EmployeeService employeeService = mock(EmployeeService.class);
    private final EmployeeController employeeController = new EmployeeController(employeeService);

    @Test
    void shouldReturnAllEmployees() {
        EmployeeResponseDTO employee = new EmployeeResponseDTO(
                1L, "EMP001", "Ahmed", "Alami", "ahmed@example.com",
                "Developpeur", LocalDate.of(2024, 1, 1),
                1L, "IT", null, null, null);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<EmployeeResponseDTO> employees = new PageImpl<>(List.of(employee));
        when(employeeService.getAllEmployees(null, null, pageable)).thenReturn(employees);


        Page<EmployeeResponseDTO> result = employeeController.list(null, null, pageable);

        assertEquals(List.of(employee), result.getContent());
        verify(employeeService).getAllEmployees(null, null, pageable);
    }
}
