package com.example.splitapp.controller;

import com.example.splitapp.dto.ApiResponseDTO;
import com.example.splitapp.dto.ExpenseRequestDTO;
import com.example.splitapp.entity.Expense;
import com.example.splitapp.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Expense>> addExpense(@Valid @RequestBody ExpenseRequestDTO request) {
        Expense newExpense = expenseService.addExpense(request);
        return new ResponseEntity<>(new ApiResponseDTO<>(true, "Expense added successfully.", newExpense), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Expense>>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "All expenses retrieved.", expenses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Expense>> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequestDTO request) {
        Expense updatedExpense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Expense updated successfully.", updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Object>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Expense deleted successfully.", null));
    }
}