package com.hs.lab3.eventservice.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateUserEventRequest(
        @NotBlank
        String name,
        String description,
        @NotNull
        LocalDate date,
        @NotNull
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @NotNull
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime
) {}
