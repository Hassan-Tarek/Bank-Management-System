package com.bms.repository;

import com.bms.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends
        JpaRepository<Payment, Long> {

    List<Payment> findByLoanId(Long loanId);

    List<Payment> findByAccountNumber(String accountNumber);

    @Query("SELECT p FROM Payment AS p WHERE p.account.customer.id = :customerId")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);
}
