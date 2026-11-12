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

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapCreateServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).createDepartment(dto);
        mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).createDepartment(dto);
    }

    @Test
    void shouldUpdate() throws Exception {
        var expected = response;
        when(service.updateDepartment(1L, dto)).thenReturn(expected);
        mvc.perform(put("/api/departments/1").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).updateDepartment(1L, dto);
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
        doThrow(error).when(service).updateDepartment(1L, dto);
        mvc.perform(put("/api/departments/1").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).updateDepartment(1L, dto);
    }

    @Test
    void shouldGet() throws Exception {
        var expected = response;
        when(service.getDepartmentById(1L)).thenReturn(expected);
        mvc.perform(get("/api/departments/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).getDepartmentById(1L);
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
        doThrow(error).when(service).getDepartmentById(1L);
        mvc.perform(get("/api/departments/1")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).getDepartmentById(1L);
    }

    @Test
    void shouldList() throws Exception {
        var expected = List.of(response);
        when(service.getAllDepartments()).thenReturn(expected);
        mvc.perform(get("/api/departments"))
                .andExpect(status().is(200))
                .andExpect(content().json(json.writeValueAsString(expected), true));
        verify(service).getAllDepartments();
        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        when(service.getAllDepartments()).thenReturn(List.of());
        mvc.perform(get("/api/departments")).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(service).getAllDepartments();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapListServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).getAllDepartments();
        mvc.perform(get("/api/departments")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).getAllDepartments();
    }

    @Test
    void shouldDelete() throws Exception {
        mvc.perform(delete("/api/departments/1"))
                .andExpect(status().is(204))
                .andExpect(content().string(""));
        verify(service).deleteDepartment(1L);
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void shouldMapDeleteServiceErrors(int statusCode) throws Exception {
        RuntimeException error = switch (statusCode) {
            case 400 -> new IllegalArgumentException("Invalid request");
            case 404 -> new ResourceNotFoundException("Not found");
            default -> new IllegalStateException("Conflict");
        };
        doThrow(error).when(service).deleteDepartment(1L);
        mvc.perform(delete("/api/departments/1")).andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value(error.getMessage()));
        verify(service).deleteDepartment(1L);
    }

}
