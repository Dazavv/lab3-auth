package com.hs.lab3.groupeventservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateGroupEventRequest(
        @NotBlank String name,
        String description,
        @NotNull List<Long> participantIds,
        @NotNull Long ownerId
) {
}
