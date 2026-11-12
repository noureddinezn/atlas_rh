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
class ManagerControllerTest extends ControllerTestSupport {
    @Mock private ManagerService service;
    private LeaveDecisionDTO dto;
    private LeaveRequestResponseDTO response;
    private static final String VALID = "{\"status\":\"APPROUVEE\",\"comment\":\"Approved\"}";

    @BeforeEach
    void setUp() throws Exception {
        setUpController(new ManagerController(service));
        dto = json.readValue(VALID, LeaveDecisionDTO.class);
        response = json.readValue("{\"id\":1,\"employeeId\":2,\"status\":\"APPROUVEE\",\"comment\":\"Approved\",\"workingDays\":2}", LeaveRequestResponseDTO.class);
    }

    @Test
    void shouldPending() throws Exception {
        var expected = List.of(response);
        when(service.pending(3L)).thenReturn(expected);
        mvc.perform(get("/api/managers/3/leave-requests/pending"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).pending(3L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyPending() throws Exception {
        when(service.pending(3L)).thenReturn(List.of());
        mvc.perform(get("/api/managers/3/leave-requests/pending")).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(service).pending(3L);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapPendingServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).pending(3L);
        mvc.perform(get("/api/managers/3/leave-requests/pending")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).pending(3L);
    }

    @Test
    void shouldDecision() throws Exception {
        var expected = response;
        when(service.decide(3L, 1L, dto)).thenReturn(expected);
        mvc.perform(patch("/api/managers/leave-requests/1/decision").param("managerId", "3").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).decide(3L, 1L, dto);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapDecisionServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).decide(3L, 1L, dto);
        mvc.perform(patch("/api/managers/leave-requests/1/decision").param("managerId", "3").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).decide(3L, 1L, dto);
    }

    static Stream<String> invalidBodies() {
        return Stream.of(
                "{\"status\":null,\"comment\":\"Approved\"}",
                "{\"status\":\"APPROUVEE\",\"comment\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"}"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    void shouldRejectInvalidDecisionBody(String body) throws Exception {
        mvc.perform(patch("/api/managers/leave-requests/1/decision").param("managerId", "3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"unknown\":true}"})
    void shouldRejectMalformedOrMissingDecisionBody(String body) throws Exception {
        mvc.perform(patch("/api/managers/leave-requests/1/decision").param("managerId", "3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void shouldRequireManagerForDecision() throws Exception {
        mvc.perform(patch("/api/managers/leave-requests/1/decision")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void shouldRejectUnknownDecisionStatus() throws Exception {
        mvc.perform(patch("/api/managers/leave-requests/1/decision").param("managerId", "3")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"UNKNOWN\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
