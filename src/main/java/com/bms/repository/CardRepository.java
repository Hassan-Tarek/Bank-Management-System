package com.bms.repository;

import com.bms.entity.Card;
import com.bms.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

        @Query("SELECT c FROM Card AS c WHERE (:status IS NULL OR c.status = :status)")
        Page<Card> findAllCards(@Param("status") CardStatus status, Pageable pageable);

        List<Card> findByAccountCustomerId(Long customerId);

        Optional<Card> findByNumber(String number);

        boolean existsByNumber(String number);

        boolean existsByAccountId(Long accountId);
}
