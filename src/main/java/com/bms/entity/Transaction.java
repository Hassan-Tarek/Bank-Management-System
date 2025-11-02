package com.bms.entity;

import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    @Builder.Default
    @Column(name = "transaction_reference", updatable = false, insertable = false)
    private String transactionReference = UUID.randomUUID().toString();

    @Column(name = "transaction_date", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sender_id",
            foreignKey = @ForeignKey(name = "fk_transactions_sender")
    )
    @ToString.Exclude
    @JsonIgnore
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "receiver_id",
            foreignKey = @ForeignKey(name = "fk_transactions_receiver")
    )
    @ToString.Exclude
    @JsonIgnore
    private Account receiver;
}
