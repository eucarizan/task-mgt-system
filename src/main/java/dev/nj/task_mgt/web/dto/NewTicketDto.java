package dev.nj.task_mgt.web.dto;

import jakarta.validation.constraints.NotBlank;

public record NewTicketDto(
        @NotBlank(message = "title should not be blank/null/empty")
        String title,
        @NotBlank(message = "description should not be blank/null/empty")
        String description
) {
}
