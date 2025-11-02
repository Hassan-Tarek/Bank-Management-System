package com.bms.repository;

import com.bms.entity.Payment;
import com.bms.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends
        JpaRepository<Payment, Long> {

    @Query("""
        SELECT p FROM Payment AS p
        WHERE (:status IS NULL OR p.paymentStatus = :status)
            AND (:date IS NULL OR p.paymentDate = :date)
    """)
    List<Payment> findByFilters(
            @Param("status") PaymentStatus status,
            @Param("date") LocalDate date
    );

    List<Payment> findByLoanId(Long loanId);

    @Query("SELECT p FROM Payment AS p WHERE p.account.user.id = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);
}
