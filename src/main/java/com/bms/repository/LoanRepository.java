package com.bms.repository;

import com.bms.entity.Loan;
import com.bms.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends
        JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan AS l WHERE (:status IS NULL OR l.loanStatus = :status)")
    List<Loan> findByFilters(@Param("status") LoanStatus loanStatus);

    @Query("SELECT l FROM Loan AS l WHERE l.account.user.id = :userId")
    List<Loan> findByUserId(@Param("userId") Long userId);
}
