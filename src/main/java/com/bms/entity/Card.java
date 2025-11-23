package com.bms.entity;

import com.bms.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "cards",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"number"})
        }
)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @OneToOne
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(name = "fk_cards_account")
    )
    @JsonIgnore
    private Account account;
}
