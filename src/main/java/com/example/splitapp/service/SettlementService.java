package com.example.splitapp.service;

import com.example.splitapp.dto.BalanceDTO;
import com.example.splitapp.dto.SettlementDTO;
import com.example.splitapp.entity.User;
import com.example.splitapp.repository.ExpenseRepository;
import com.example.splitapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public List<User> getAllPeople() {
        return userRepository.findAll();
    }

    public List<BalanceDTO> getBalances() {
        Map<String, BigDecimal> balanceMap = new HashMap<>();
        userRepository.findAll().forEach(user -> balanceMap.put(user.getName(), BigDecimal.ZERO));

        expenseRepository.findAll().forEach(expense -> {
            String paidBy = expense.getPaidBy().getName();
            BigDecimal amount = expense.getTotalAmount();
            balanceMap.put(paidBy, balanceMap.get(paidBy).add(amount));

            expense.getSplits().forEach(split -> {
                String owedBy = split.getOwedBy().getName();
                BigDecimal owedAmount = split.getAmountOwed();
                balanceMap.put(owedBy, balanceMap.get(owedBy).subtract(owedAmount));
            });
        });

        return balanceMap.entrySet().stream()
                .map(entry -> new BalanceDTO(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP)))
                .collect(Collectors.toList());
    }

    public List<SettlementDTO> getSettlements() {
        List<BalanceDTO> balances = getBalances();
        List<SettlementDTO> settlements = new ArrayList<>();

        Map<String, BigDecimal> creditors = balances.stream()
                .filter(b -> b.balance().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toMap(BalanceDTO::userName, BalanceDTO::balance));

        Map<String, BigDecimal> debtors = balances.stream()
                .filter(b -> b.balance().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toMap(BalanceDTO::userName, b -> b.balance().abs()));

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            String maxCreditorName = Collections.max(creditors.entrySet(), Map.Entry.comparingByValue()).getKey();
            String maxDebtorName = Collections.max(debtors.entrySet(), Map.Entry.comparingByValue()).getKey();

            BigDecimal creditorAmount = creditors.get(maxCreditorName);
            BigDecimal debtorAmount = debtors.get(maxDebtorName);

            BigDecimal settlementAmount = creditorAmount.min(debtorAmount);

            settlements.add(new SettlementDTO(maxDebtorName, maxCreditorName, settlementAmount));

            creditors.compute(maxCreditorName, (k, v) -> v.subtract(settlementAmount));
            debtors.compute(maxDebtorName, (k, v) -> v.subtract(settlementAmount));

            creditors.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
            debtors.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
        }
        return settlements;
    }
}