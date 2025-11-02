package com.bms.controller;

import com.bms.dto.LoanDto;
import com.bms.enums.LoanStatus;
import com.bms.security.CustomUserDetails;
import com.bms.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/loans",
        produces = "application/json")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDto>> getLoansByFilters(
            @RequestParam(required = false) LoanStatus loanStatus) {
        List<LoanDto> loans = loanService.getLoansByFilters(loanStatus);
        return ResponseEntity.ok(loans);
    }

    @GetMapping(path = "/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDto>> getLoansByUserId(@PathVariable Long userId) {
        List<LoanDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping(path = "/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LoanDto>> getMyLoans(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        List<LoanDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @PostMapping(path = "/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LoanDto> applyForLoan(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid LoanDto loanDto) {
        Long userId = customUserDetails.getUser().getId();
        LoanDto createdLoan = loanService.applyForLoan(userId, loanDto);
        return ResponseEntity.ok(createdLoan);
    }

    @PatchMapping(path = "/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanDto> approveLoan(@PathVariable Long loanId) {
        LoanDto approvedLoan = loanService.approveLoan(loanId);
        return ResponseEntity.ok(approvedLoan);
    }

    @PatchMapping(path = "/{loanId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanDto> rejectLoan(@PathVariable Long loanId) {
        LoanDto rejectedLoan = loanService.rejectLoan(loanId);
        return ResponseEntity.ok(rejectedLoan);
    }

    @PatchMapping(path = "/{loanId}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanDto> disburseLoan(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long loanId) {
        Long userId = customUserDetails.getUser().getId();
        LoanDto updatedLoan = loanService.disburseLoan(userId, loanId);
        return ResponseEntity.ok(updatedLoan);
    }
}
