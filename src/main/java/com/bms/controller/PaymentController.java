package com.bms.controller;

import com.bms.dto.request.PaymentRequestDto;
import com.bms.dto.response.PaymentResponseDto;
import com.bms.security.CustomUserDetails;
import com.bms.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/payments",
        produces = "application/json")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(path = "/pay")
    public ResponseEntity<PaymentResponseDto> makePayment(
            @Valid @RequestBody PaymentRequestDto paymentRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        PaymentResponseDto payment = paymentService.makePayment(paymentRequestDto, customerId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<PaymentResponseDto>> getAllPayments(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PaymentResponseDto> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping(path = "/loan/{loanId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByLoanId(
            @PathVariable Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<PaymentResponseDto> payments = paymentService.getPaymentsByLoanId(loanId, customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping(path = "/account/{accountNumber}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByAccountNumber(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<PaymentResponseDto> payments = paymentService.getPaymentsByAccountNumber(accountNumber, customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping(path = "/customer/{customerId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByCustomerId(
            @PathVariable Long customerId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        List<PaymentResponseDto> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }
}
