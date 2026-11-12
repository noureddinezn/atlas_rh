package com.atlasrh.api.controller;

import com.atlasrh.api.dto.request.*;
import com.atlasrh.api.dto.response.*;
import com.atlasrh.api.entity.enums.*;
import com.atlasrh.api.exception.ResourceNotFoundException;
import com.atlasrh.api.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest extends ControllerTestSupport {
    @Mock private DepartmentService service;
    private DepartmentRequestDTO dto;
    private DepartmentResponseDTO response;
    private static final String VALID = "{\"name\":\"IT\",\"description\":\"Engineering\"}";

    @BeforeEach
    void setUp() throws Exception {
        setUpController(new DepartmentController(service));
        dto = json.readValue(VALID, DepartmentRequestDTO.class);
        response = json.readValue("{\"id\":1,\"name\":\"IT\",\"description\":\"Engineering\",\"employeeCount\":2}", DepartmentResponseDTO.class);
    }

    @Test
    void shouldCreate() throws Exception {
        var expected = response;
        when(service.createDepartment(dto)).thenReturn(expected);
        mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(201))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).createDepartment(dto);
        verifyNoMoreInteractions(service);
    }

}
