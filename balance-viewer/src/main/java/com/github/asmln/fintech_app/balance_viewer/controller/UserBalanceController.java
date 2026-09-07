package com.github.asmln.fintech_app.balance_viewer.controller;

import com.github.asmln.fintech_app.balance_viewer.dto.BalanceDto;
import com.github.asmln.fintech_app.balance_viewer.service.UserBalanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balances")
public class UserBalanceController {
    private final UserBalanceService userBalanceService;

    public UserBalanceController(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    @GetMapping
    public ResponseEntity<Page<BalanceDto>> getUsers(@PageableDefault(sort = "userId") Pageable pageable) {
        Page<BalanceDto> balanceDtoPage = userBalanceService.obtainBalancesPage(pageable);
        return ResponseEntity.ok(balanceDtoPage);
    }
}
