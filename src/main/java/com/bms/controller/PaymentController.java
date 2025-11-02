package com.bms.controller;

import com.bms.dto.PaymentDto;
import com.bms.enums.PaymentStatus;
import com.bms.security.CustomUserDetails;
import com.bms.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDto>> getPaymentsByFilters(
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) LocalDate paymentDate) {
        List<PaymentDto> payments = paymentService.getPaymentsByFilters(paymentStatus, paymentDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getPaymentsByLoan(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long loanId) {
        Long userId = customUserDetails.getUser().getId();
        List<PaymentDto> payments = paymentService.getPaymentsByLoanId(userId, loanId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long userId = customUserDetails.getUser().getId();
        List<PaymentDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDto> payLoan(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid PaymentDto paymentDto) {
        Long userId = customUserDetails.getUser().getId();
        PaymentDto payment = paymentService.payLoan(userId, paymentDto);
        return ResponseEntity.ok(payment);
    }
}
