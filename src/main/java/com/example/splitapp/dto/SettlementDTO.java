package com.example.splitapp.dto;
import java.math.BigDecimal;
public record SettlementDTO(String fromUser, String toUser, BigDecimal amount) {}