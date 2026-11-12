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
class ContractControllerTest extends ControllerTestSupport {
    @Mock private ContractService service;
    private ContractRequestDTO dto;
    private ContractResponseDTO response;
    private static final String VALID = "{\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":10000,\"employeeId\":2}";

    @BeforeEach
    void setUp() throws Exception {
        setUpController(new ContractController(service));
        dto = json.readValue(VALID, ContractRequestDTO.class);
        response = json.readValue("{\"id\":1,\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":10000,\"active\":true,\"employeeId\":2,\"employeeFirstName\":\"Ahmed\"}", ContractResponseDTO.class);
    }

    @Test
    void shouldCreate() throws Exception {
        var expected = response;
        when(service.createContract(dto)).thenReturn(expected);
        mvc.perform(post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(201))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).createContract(dto);
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
        doThrow(error).when(service).createContract(dto);
        mvc.perform(post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).createContract(dto);
    }

    @Test
    void shouldTerminate() throws Exception {
        var expected = response;
        when(service.terminateContract(1L)).thenReturn(expected);
        mvc.perform(patch("/api/contracts/1/terminate"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).terminateContract(1L);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapTerminateServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).terminateContract(1L);
        mvc.perform(patch("/api/contracts/1/terminate")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).terminateContract(1L);
    }

    @Test
    void shouldHistory() throws Exception {
        var expected = List.of(response);
        when(service.getEmployeeContractHistory(2L)).thenReturn(expected);
        mvc.perform(get("/api/contracts/employee/2"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).getEmployeeContractHistory(2L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyHistory() throws Exception {
        when(service.getEmployeeContractHistory(2L)).thenReturn(List.of());
        mvc.perform(get("/api/contracts/employee/2")).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(service).getEmployeeContractHistory(2L);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapHistoryServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).getEmployeeContractHistory(2L);
        mvc.perform(get("/api/contracts/employee/2")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).getEmployeeContractHistory(2L);
    }

    static Stream<String> invalidBodies() {
        return Stream.of(
                "{\"type\":null,\"startDate\":\"2024-01-01\",\"grossSalary\":10000,\"employeeId\":2}",
                "{\"type\":\"CDI\",\"startDate\":null,\"grossSalary\":10000,\"employeeId\":2}",
                "{\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":null,\"employeeId\":2}",
                "{\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":0,\"employeeId\":2}",
                "{\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":-1,\"employeeId\":2}",
                "{\"type\":\"CDI\",\"startDate\":\"2024-01-01\",\"grossSalary\":10000,\"employeeId\":null}"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldRejectInvalidCreateBody(String body) throws Exception {
        mvc.perform(post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"unknown\":true}"})
    void shouldRejectMalformedOrMissingCreateBody(String body) throws Exception {
        mvc.perform(post("/api/contracts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
