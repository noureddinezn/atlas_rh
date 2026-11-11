package com.atlasrh.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
public record TerminationRequest(@NotNull LocalDate terminationDate) {}
