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
class EmployeeControllerTest extends ControllerTestSupport {
    @Mock private EmployeeService service;
    private EmployeeRequestDTO dto;
    private EmployeeResponseDTO response;
    private static final String VALID = "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}";

    @BeforeEach
    void setUp() throws Exception {
        setUpController(new EmployeeController(service));
        dto = json.readValue(VALID, EmployeeRequestDTO.class);
        response = json.readValue("{\"id\":1,\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"departmentName\":\"IT\",\"managerId\":3}", EmployeeResponseDTO.class);
    }

    @Test
    void shouldCreate() throws Exception {
        var expected = response;
        when(service.createEmployee(dto)).thenReturn(expected);
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(201))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).createEmployee(dto);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapCreateServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).createEmployee(dto);
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).createEmployee(dto);
    }

    @Test
    void shouldUpdate() throws Exception {
        var expected = response;
        when(service.updateEmployee(1L, dto)).thenReturn(expected);
        mvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).updateEmployee(1L, dto);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapUpdateServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).updateEmployee(1L, dto);
        mvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).updateEmployee(1L, dto);
    }

    @Test
    void shouldGet() throws Exception {
        var expected = response;
        when(service.getEmployeeById(1L)).thenReturn(expected);
        mvc.perform(get("/api/employees/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).getEmployeeById(1L);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapGetServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).getEmployeeById(1L);
        mvc.perform(get("/api/employees/1")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).getEmployeeById(1L);
    }

    @Test
    void shouldTeam() throws Exception {
        var expected = List.of(response);
        when(service.getManagerTeam(3L)).thenReturn(expected);
        mvc.perform(get("/api/employees/manager/3/team"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).getManagerTeam(3L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyTeam() throws Exception {
        when(service.getManagerTeam(3L)).thenReturn(List.of());
        mvc.perform(get("/api/employees/manager/3/team")).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(service).getManagerTeam(3L);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapTeamServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).getManagerTeam(3L);
        mvc.perform(get("/api/employees/manager/3/team")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).getManagerTeam(3L);
    }

    static Stream<String> invalidBodies() {
        return Stream.of(
                "{\"matricule\":null,\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\" \",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":null,\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\" \",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":null,\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\" \",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":null,\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\" \",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"invalid\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":null,\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\" \",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"hireDate\":\"2024-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":null,\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2999-01-01\",\"departmentId\":2,\"managerId\":3}",
                "{\"matricule\":\"EMP001\",\"firstName\":\"Ahmed\",\"lastName\":\"Alami\",\"email\":\"ahmed@example.com\",\"position\":\"Developer\",\"hireDate\":\"2024-01-01\",\"departmentId\":null,\"managerId\":3}"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldRejectInvalidCreateBody(String body) throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"unknown\":true}"})
    void shouldRejectMalformedOrMissingCreateBody(String body) throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldRejectInvalidUpdateBody(String body) throws Exception {
        mvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"unknown\":true}"})
    void shouldRejectMalformedOrMissingUpdateBody(String body) throws Exception {
        mvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void shouldUseDefaultPaginationAndSort() throws Exception {
        when(service.getAllEmployees(isNull(), isNull(), any(Pageable.class))).thenReturn(Page.empty(PageRequest.of(0, 10, Sort.by("id"))));
        mvc.perform(get("/api/employees")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        verify(service).getAllEmployees(null, null, PageRequest.of(0, 10, Sort.by("id")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "matricule", "firstName", "lastName", "email", "position", "hireDate"})
    void shouldForwardFiltersPaginationAndAllowedSort(String field) throws Exception {
        PageRequest pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, field));
        when(service.getAllEmployees("Ahmed", 2L, pageable))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 6));
        mvc.perform(get("/api/employees").param("name", "Ahmed").param("departmentId", "2")
                        .param("page", "1").param("size", "5").param("sort", field + ",desc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.number").value(1)).andExpect(jsonPath("$.totalElements").value(6));
        verify(service).getAllEmployees("Ahmed", 2L, pageable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"salary", "department.name", "ID", "managerId"})
    void shouldRejectUnsupportedSort(String field) throws Exception {
        mvc.perform(get("/api/employees").param("sort", "id,asc").param("sort", field + ",desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported sort field"));
        verifyNoInteractions(service);
    }

    @Test
    void shouldRejectInvalidDepartmentParameter() throws Exception {
        mvc.perform(get("/api/employees").param("departmentId", "invalid")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void shouldRejectNonNumericId() throws Exception {
        mvc.perform(get("/api/employees/invalid")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
