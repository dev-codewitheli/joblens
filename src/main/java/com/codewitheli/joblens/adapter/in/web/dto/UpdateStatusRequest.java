package com.codewitheli.joblens.adapter.in.web.dto;

import com.codewitheli.joblens.domain.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/** Request body for moving an application to a new Kanban column. */
public record UpdateStatusRequest(
        @NotNull(message = "status is required")
        ApplicationStatus status
) {
}
