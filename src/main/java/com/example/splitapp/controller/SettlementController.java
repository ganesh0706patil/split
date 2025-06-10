package com.example.splitapp.controller;

import com.example.splitapp.dto.ApiResponseDTO;
import com.example.splitapp.dto.BalanceDTO;
import com.example.splitapp.dto.SettlementDTO;
import com.example.splitapp.entity.User;
import com.example.splitapp.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping("/balances")
    public ResponseEntity<ApiResponseDTO<List<BalanceDTO>>> getBalances() {
        List<BalanceDTO> balances = settlementService.getBalances();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Balances retrieved successfully.", balances));
    }

    @GetMapping("/settlements")
    public ResponseEntity<ApiResponseDTO<List<SettlementDTO>>> getSettlements() {
        List<SettlementDTO> settlements = settlementService.getSettlements();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Settlements calculated successfully.", settlements));
    }

    @GetMapping("/people")
    public ResponseEntity<ApiResponseDTO<List<User>>> getAllPeople() {
        List<User> people = settlementService.getAllPeople();
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "All people retrieved successfully.", people));
    }
}