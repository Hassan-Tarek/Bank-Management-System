package com.bms.repository;

import com.bms.entity.Loan;
import com.bms.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends
        JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan AS l WHERE (:status IS NULL OR l.status = :status)")
    Page<Loan> findAllLoans(@Param("status") LoanStatus status, Pageable pageable);

    List<Loan> findByCustomerId(Long customerId);

    @Query("""
        SELECT COUNT(l) > 0 FROM Loan l
        WHERE l.customer.id = :customerId
          AND l.status = com.bms.enums.LoanStatus.DISBURSED
    """)
    boolean existsActiveLoanByCustomerId(Long customerId);
}
