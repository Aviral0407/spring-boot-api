package com.aviral.spring_boot_api.controller;

import com.aviral.spring_boot_api.model.*;
import com.aviral.spring_boot_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinanceController {

    private final MonthRepository monthRepo;
    private final CategoryRepository categoryRepo;
    private final TransactionRepository transactionRepo;

    // GET or CREATE month
    @GetMapping("/month/{monthName}")
    public ResponseEntity<Month> getOrCreateMonth(@PathVariable String monthName) {
        Month month = monthRepo.findByMonthName(monthName)
                .orElseGet(() -> {
                    Month m = new Month();
                    m.setMonthName(monthName);
                    return monthRepo.save(m);
                });
        return ResponseEntity.ok(month);
    }

    // UPDATE income & savings
    @PutMapping("/month/{monthName}")
    public ResponseEntity<Month> updateMonth(@PathVariable String monthName, @RequestBody Month body) {
        Month month = monthRepo.findByMonthName(monthName)
                .orElseGet(() -> { Month m = new Month(); m.setMonthName(monthName); return m; });
        if (body.getIncome() != null) month.setIncome(body.getIncome());
        if (body.getSavingsPercent() != null) month.setSavingsPercent(body.getSavingsPercent());
        return ResponseEntity.ok(monthRepo.save(month));
    }

    // GET categories
    @GetMapping("/month/{monthName}/categories")
    public ResponseEntity<List<Category>> getCategories(@PathVariable String monthName) {
        Month month = monthRepo.findByMonthName(monthName).orElse(null);
        if (month == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(categoryRepo.findByMonthId(month.getId()));
    }

    // ADD category
    @PostMapping("/month/{monthName}/categories")
    public ResponseEntity<Category> addCategory(@PathVariable String monthName, @RequestBody Category body) {
        Month month = monthRepo.findByMonthName(monthName)
                .orElseGet(() -> { Month m = new Month(); m.setMonthName(monthName); return monthRepo.save(m); });
        body.setMonth(month);
        return ResponseEntity.ok(categoryRepo.save(body));
    }

    // DELETE category
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // UPDATE category
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category body) {
        Category cat = categoryRepo.findById(id).orElseThrow();
        if (body.getName() != null) cat.setName(body.getName());
        if (body.getBudget() != null) cat.setBudget(body.getBudget());
        return ResponseEntity.ok(categoryRepo.save(cat));
    }

    // GET transactions
    @GetMapping("/categories/{categoryId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long categoryId) {
        return ResponseEntity.ok(transactionRepo.findByCategoryId(categoryId));
    }

    // ADD transaction
    @PostMapping("/categories/{categoryId}/transactions")
    public ResponseEntity<Transaction> addTransaction(@PathVariable Long categoryId, @RequestBody Transaction body) {
        Category cat = categoryRepo.findById(categoryId).orElseThrow();
        body.setCategory(cat);
        return ResponseEntity.ok(transactionRepo.save(body));
    }

    // DELETE transaction
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/{monthName}")
    public ResponseEntity<?> getDashboard(@PathVariable String monthName) {
        Month month = monthRepo.findByMonthName(monthName)
                .orElseGet(() -> {
                    Month m = new Month();
                    m.setMonthName(monthName);
                    return monthRepo.save(m);
                });

        List<Category> categories = categoryRepo.findByMonthId(month.getId());

        List<Map<String, Object>> catData = categories.stream().map(cat -> {
            List<Transaction> txns = transactionRepo.findByCategoryId(cat.getId());
            double spent = txns.stream().mapToDouble(Transaction::getAmount).sum();

            Map<String, Object> catMap = new java.util.HashMap<>();
            catMap.put("id", cat.getId());
            catMap.put("name", cat.getName());
            catMap.put("budget", cat.getBudget());
            catMap.put("spent", spent);
            catMap.put("remaining", cat.getBudget() - spent);
            catMap.put("transactions", txns);
            return catMap;
        }).collect(java.util.stream.Collectors.toList());

        double totalExpense = catData.stream()
                .mapToDouble(c -> (Double) c.get("spent")).sum();
        double savings = month.getIncome() * (month.getSavingsPercent() / 100);
        double remaining = month.getIncome() - savings - totalExpense;

        Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("monthName", month.getMonthName());
        dashboard.put("income", month.getIncome());
        dashboard.put("savingsPercent", month.getSavingsPercent());
        dashboard.put("savings", savings);
        dashboard.put("totalExpense", totalExpense);
        dashboard.put("remaining", remaining);
        dashboard.put("categories", catData);

        return ResponseEntity.ok(dashboard);
    }

    // GET all months
    @GetMapping("/months")
    public ResponseEntity<List<Month>> getAllMonths() {
        return ResponseEntity.ok(monthRepo.findAll());
    }
}