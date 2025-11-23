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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"reference"})
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "reference", nullable = false, unique = true, updatable = false)
    private String reference = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "fee", nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sender_account_id",
            foreignKey = @ForeignKey(name = "fk_transactions_sender")
    )
    @ToString.Exclude
    @JsonIgnore
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "receiver_account_id",
            foreignKey = @ForeignKey(name = "fk_transactions_receiver")
    )
    @ToString.Exclude
    @JsonIgnore
    private Account receiver;
}
