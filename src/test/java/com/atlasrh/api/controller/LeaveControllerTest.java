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
class LeaveControllerTest extends ControllerTestSupport {
    @Mock private LeaveService service;
    private LeaveRequestCreateDTO dto;
    private LeaveRequestResponseDTO response;
    private static final String VALID = "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}";

    @BeforeEach
    void setUp() throws Exception {
        setUpController(new LeaveController(service));
        dto = json.readValue(VALID, LeaveRequestCreateDTO.class);
        response = json.readValue("{\"id\":1,\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\",\"status\":\"EN_ATTENTE\",\"workingDays\":2}", LeaveRequestResponseDTO.class);
    }

    @Test
    void shouldCreate() throws Exception {
        var expected = response;
        when(service.create(dto)).thenReturn(expected);
        mvc.perform(post("/api/leaves").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(201))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).create(dto);
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
        doThrow(error).when(service).create(dto);
        mvc.perform(post("/api/leaves").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).create(dto);
    }

    @Test
    void shouldList() throws Exception {
        var expected = List.of(response);
        when(service.list(2L, null)).thenReturn(expected);
        mvc.perform(get("/api/leaves/employee/2"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).list(2L, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        when(service.list(2L, null)).thenReturn(List.of());
        mvc.perform(get("/api/leaves/employee/2")).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(service).list(2L, null);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapListServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).list(2L, null);
        mvc.perform(get("/api/leaves/employee/2")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).list(2L, null);
    }

    @Test
    void shouldCancel() throws Exception {
        var expected = response;
        when(service.cancel(1L, 2L)).thenReturn(expected);
        mvc.perform(patch("/api/leaves/1/cancel").param("employeeId", "2"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).cancel(1L, 2L);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapCancelServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).cancel(1L, 2L);
        mvc.perform(patch("/api/leaves/1/cancel").param("employeeId", "2")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).cancel(1L, 2L);
    }

    @Test
    void shouldBalance() throws Exception {
        var expected = new LeaveBalanceDTO(2L, 2026, java.time.Period.ofYears(2), List.of(new LeaveBalanceDTO.TypeBalance(1L, "Annual", 22, 2, 20)));
        when(service.balance(2L)).thenReturn(expected);
        mvc.perform(get("/api/leaves/employee/2/balance"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).balance(2L);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapBalanceServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).balance(2L);
        mvc.perform(get("/api/leaves/employee/2/balance")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).balance(2L);
    }

    static Stream<String> invalidBodies() {
        return Stream.of(
                "{\"employeeId\":null,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":0,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":-1,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":null,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":0,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":-1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":null,\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":null,\"reason\":\"Holiday\"}",
                "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":null}",
                "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\" \"}",
                "{\"employeeId\":2,\"leaveTypeId\":1,\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"}"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldRejectInvalidCreateBody(String body) throws Exception {
        mvc.perform(post("/api/leaves").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"unknown\":true}"})
    void shouldRejectMalformedOrMissingCreateBody(String body) throws Exception {
        mvc.perform(post("/api/leaves").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"EN_ATTENTE", "APPROUVEE", "REFUSEE", "ANNULEE"})
    void shouldFilterByStatus(String value) throws Exception {
        RequestStatus filter = RequestStatus.valueOf(value);
        when(service.list(2L, filter)).thenReturn(List.of(response));
        mvc.perform(get("/api/leaves/employee/2").param("status", value))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
        verify(service).list(2L, filter);
    }

    @Test
    void shouldRejectUnknownStatus() throws Exception {
        mvc.perform(get("/api/leaves/employee/2").param("status", "UNKNOWN")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void shouldRequireEmployeeForCancellation() throws Exception {
        mvc.perform(patch("/api/leaves/1/cancel")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
