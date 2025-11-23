package com.bms.entity;

import com.bms.enums.LoanStatus;
import com.bms.enums.LoanType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LoanType type;

    @Column(name = "principal_amount", nullable = false)
    private BigDecimal principalAmount;

    @Column(name = "remaining_amount", nullable = false)
    private BigDecimal remainingAmount;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            foreignKey = @ForeignKey(name = "fk_loans_customer")
    )
    @JsonIgnore
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "disbursement_account_id",
            foreignKey = @ForeignKey(name = "fk_loans_disbursement_account")
    )
    @JsonIgnore
    private Account disbursementAccount;
}
