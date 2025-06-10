package com.example.splitapp.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

// For EXACT and PERCENTAGE splits, 'value' is required. For EQUAL, it's ignored.
public record SplitDetailDTO(
        @NotBlank(message = "User name in split cannot be blank")
        String userName,

        BigDecimal value // Represents amount for EXACT, percentage for PERCENTAGE
) {}