package com.bms.controller;

import com.bms.dto.request.LoanRequestDto;
import com.bms.dto.response.LoanResponseDto;
import com.bms.enums.LoanStatus;
import com.bms.security.CustomUserDetails;
import com.bms.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping(path = "/all")
    public ResponseEntity<Page<LoanResponseDto>> getAllLoans(
            @RequestParam(required = false) LoanStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LoanResponseDto> loans = loanService.getAllLoans(status, pageable);
        return ResponseEntity.ok(loans);
    }

    @GetMapping(path = "/customer/{customerId}")
    public ResponseEntity<List<LoanResponseDto>> getLoansByCustomerId(
            @PathVariable Long customerId) {
        List<LoanResponseDto> loans = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<LoanResponseDto> loans = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(loans);
    }

    @PostMapping(path = "/apply")
    public ResponseEntity<LoanResponseDto> applyForLoan(
            @Valid @RequestBody LoanRequestDto loanRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        LoanResponseDto loan = loanService.applyForLoan(loanRequestDto, customerId);
        return ResponseEntity.ok(loan);
    }

    @PatchMapping(path = "/{loanId}/approve")
    public ResponseEntity<LoanResponseDto> approveLoan(
            @PathVariable Long loanId) {
        LoanResponseDto loan = loanService.approveLoan(loanId);
        return ResponseEntity.ok(loan);
    }

    @PatchMapping(path = "/{loanId}/reject")
    public ResponseEntity<LoanResponseDto> rejectLoan(
            @PathVariable Long loanId) {
        LoanResponseDto loan = loanService.rejectLoan(loanId);
        return ResponseEntity.ok(loan);
    }

    @PatchMapping(path = "/{loanId}/disburse")
    public ResponseEntity<LoanResponseDto> disburseLoan(
            @PathVariable Long loanId,
            @RequestParam String accountNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        LoanResponseDto loan = loanService.disburseLoan(loanId, accountNumber, customerId);
        return ResponseEntity.ok(loan);
    }
}
