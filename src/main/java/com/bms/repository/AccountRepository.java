package com.bms.repository;

import com.bms.entity.Account;
import com.bms.enums.AccountStatus;
import com.bms.enums.AccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
                WHERE (:type IS NULL OR a.type = :type)
                    AND (:status IS NULL OR a.status = :status)
                    AND (:min IS NULL OR a.balance >= :min)
                    AND (:max IS NULL OR a.balance <= :max)
            """)
    Page<Account> findAllAccounts(
            @Param("type") AccountType type,
            @Param("status") AccountStatus status,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account AS a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    Optional<Account> findByNumber(String number);

    List<Account> findByCustomerId(Long customerId);

    boolean existsByNumber(String number);
}
