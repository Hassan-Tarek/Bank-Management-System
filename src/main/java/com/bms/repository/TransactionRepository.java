package com.bms.repository;

import com.bms.entity.Transaction;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends
        JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction AS t
        WHERE (:type IS NULL OR t.transactionType = :type)
            AND (:status IS NULL OR t.transactionStatus = :status)
            AND (:date IS NULL OR t.transactionDate = :date)
    """)
    List<Transaction> findByFilters(
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("date") LocalDate date
    );

    @Query("SELECT t FROM Transaction AS t WHERE t.sender.user.id = :userId OR t.receiver.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);
}
