package com.bms.repository;

import com.bms.entity.Account;
import com.bms.entity.User;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends
        JpaRepository<Account, Long> {

    @Query("""
        SELECT a FROM Account AS a
        WHERE (:type IS NULL OR a.accountType = :type)
            AND (:status IS NULL OR a.accountStatus = :status)
            AND (:min IS NULL OR a.balance >= :min)
            AND (:max IS NULL OR a.balance <= :max)
    """)
    List<Account> findByFilters(
            @Param("type") AccountType accountType,
            @Param("status") AccountStatus accountStatus,
            @Param("min") BigDecimal minBalance,
            @Param("max") BigDecimal maxBalance
    );

    Optional<Account> findByAccountNumber(Long accountNumber);

    List<Account> findByUser(User user);
}
