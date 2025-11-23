package com.bms.repository;

import com.bms.entity.Transaction;
import com.bms.enums.TransactionStatus;
import com.bms.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends
        JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction AS t
        WHERE (:type IS NULL OR t.type = :type)
            AND (:status IS NULL OR t.status = :status)
    """)
    Page<Transaction> findAllTransactions(
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable
    );

    Optional<Transaction> findByReference(String reference);

    @Query("""
        SELECT t FROM Transaction AS t
        WHERE (t.sender IS NOT NULL AND t.sender.customer.id = :customerId)
            OR (t.receiver IS NOT NULL AND t.receiver.customer.id = :customerId)
    """)
    List<Transaction> findByCustomerId(
            @Param("customerId") Long customerId
    );

//    @Query("""
//        SELECT COUNT(t) > 0 FROM Transaction AS t
//        WHERE t.reference = :reference
//            AND (t.sender.customer.id = :customerId
//                OR t.receiver.customer.id = :customerId)
//    """)
//    boolean existsByReferenceAndCustomerId(
//            @Param("reference") String reference,
//            @Param("customerId") Long customerId
//    );
}
