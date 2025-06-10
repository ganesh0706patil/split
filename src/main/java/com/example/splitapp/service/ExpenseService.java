package com.example.splitapp.service;

import com.example.splitapp.dto.ExpenseRequestDTO;
import com.example.splitapp.dto.SplitDetailDTO;
import com.example.splitapp.entity.Expense;
import com.example.splitapp.entity.ExpenseSplit;
import com.example.splitapp.entity.User;
import com.example.splitapp.exception.ResourceNotFoundException;
import com.example.splitapp.exception.ValidationException;
import com.example.splitapp.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private static final int SCALE = 2; // For money calculations

    @Transactional
    public Expense addExpense(ExpenseRequestDTO request) {
        User paidBy = userService.findOrCreateByName(request.paidByUserName());

        Expense expense = new Expense();
        expense.setDescription(request.description());
        expense.setTotalAmount(request.totalAmount());
        expense.setPaidBy(paidBy);

        processSplits(expense, request);

        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
        }
        expenseRepository.deleteById(expenseId);
    }

    @Transactional
    public Expense updateExpense(Long expenseId, ExpenseRequestDTO request) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));

        User paidBy = userService.findOrCreateByName(request.paidByUserName());

        existingExpense.setDescription(request.description());
        existingExpense.setTotalAmount(request.totalAmount());
        existingExpense.setPaidBy(paidBy);

        // Clear old splits and process new ones
        existingExpense.getSplits().clear();
        processSplits(existingExpense, request);

        // Must flush to clear old splits before saving new ones
        expenseRepository.flush();

        return expenseRepository.save(existingExpense);
    }

    private void processSplits(Expense expense, ExpenseRequestDTO request) {
        List<SplitDetailDTO> splits = request.splits();
        int numUsers = splits.size();

        switch (request.splitType()) {
            case EQUAL:
                BigDecimal splitAmount = expense.getTotalAmount().divide(new BigDecimal(numUsers), SCALE, RoundingMode.HALF_UP);
                BigDecimal remainder = expense.getTotalAmount().subtract(splitAmount.multiply(new BigDecimal(numUsers)));

                for (int i = 0; i < numUsers; i++) {
                    SplitDetailDTO splitDetail = splits.get(i);
                    BigDecimal amount = (i == 0) ? splitAmount.add(remainder) : splitAmount; // Give remainder to first person
                    addSplitToExpense(expense, splitDetail.userName(), amount);
                }
                break;

            case EXACT:
                BigDecimal totalSplitAmount = splits.stream()
                        .map(SplitDetailDTO::value)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalSplitAmount.compareTo(expense.getTotalAmount()) != 0) {
                    throw new ValidationException("Sum of exact splits (" + totalSplitAmount + ") must equal the total expense amount (" + expense.getTotalAmount() + ").");
                }

                splits.forEach(s -> addSplitToExpense(expense, s.userName(), s.value()));
                break;

            case PERCENTAGE:
                BigDecimal totalPercentage = splits.stream()
                        .map(SplitDetailDTO::value)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
                    throw new ValidationException("Total percentage of splits must be exactly 100.");
                }

                splits.forEach(s -> {
                    BigDecimal percentage = s.value();
                    BigDecimal amount = expense.getTotalAmount().multiply(percentage).divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);
                    addSplitToExpense(expense, s.userName(), amount);
                });
                break;
        }
    }

    private void addSplitToExpense(Expense expense, String userName, BigDecimal amount) {
        User owedByUser = userService.findOrCreateByName(userName);
        ExpenseSplit split = new ExpenseSplit();
        split.setExpense(expense);
        split.setOwedBy(owedByUser);
        split.setAmountOwed(amount);
        expense.getSplits().add(split);
    }
}