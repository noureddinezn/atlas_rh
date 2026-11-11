package com.atlasrh.api.controller;

import com.atlasrh.api.dto.response.EmployeeResponseDTO;
import com.atlasrh.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @MockBean
    private EmployeeService service;

    @Autowired
    private EmployeeController controller;

    @Test
    void shouldReturnAllEmployees() {

        EmployeeResponseDTO employee = new EmployeeResponseDTO(
                1L, "EMP001", "Ahmed", "Alami", "ahmed@example.com",
                "Developpeur", LocalDate.of(2024, 1, 1),
                1L, "IT", null, null, null);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<EmployeeResponseDTO> employees = new PageImpl<>(List.of(employee));
        when(service.getAllEmployees(null, null, pageable)).thenReturn(employees);


        Page<EmployeeResponseDTO> result = controller.list(null, null, pageable);

        assertEquals(List.of(employee), result.getContent());
        verify(service).getAllEmployees(null, null, pageable);
    }
}
