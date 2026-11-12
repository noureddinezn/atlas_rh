package com.atlasrh.api.controller;

import com.atlasrh.api.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

abstract class ControllerTestSupport {
    protected final ObjectMapper json = JsonMapper.builder().findAndAddModules().build();
    protected MockMvc mvc;

    protected void setUpController(Object controller) {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }
}
