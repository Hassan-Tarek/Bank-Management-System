package com.bms.entity;

import com.bms.enums.LoanStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "principal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "installment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "installment_count", nullable = false)
    private Integer installmentCount;

    @Column(name = "remaining_installments", nullable = false)
    private Integer remainingInstallments;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status", nullable = false)
    private LoanStatus loanStatus = LoanStatus.PENDING;

    @Column(name = "next_due_date", nullable = false)
    private LocalDateTime nextDueDate;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_loans_user_id")
    )
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(name = "fk_loans_account_id")
    )
    @ToString.Exclude
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();
}
