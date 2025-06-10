package com.example.splitapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ExpenseRequestDTO(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmount,

        @NotBlank(message = "PaidBy user is required")
        String paidByUserName,

        @NotNull(message = "Split type is required (EQUAL, EXACT, or PERCENTAGE)")
        SplitType splitType,

        @NotEmpty(message = "At least one user must be included in the split")
        List<@Valid SplitDetailDTO> splits
) {}