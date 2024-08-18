package dev.nj.task_mgt.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewUserDto(
        @NotBlank(message = "email should not be blank")
        @Pattern(regexp = "\\w+(\\.\\w+){0,2}@\\w+\\.\\w+", message = "email incorrect format")
        String email,

        @NotBlank(message = "password should not be blank")
        @Size(min = 6, message = "password should be at least 6 characters")
        String password
) {
}
