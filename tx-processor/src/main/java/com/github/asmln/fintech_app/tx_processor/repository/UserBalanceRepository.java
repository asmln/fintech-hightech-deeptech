package com.github.asmln.fintech_app.tx_processor.repository;

import com.github.asmln.fintech_app.tx_processor.entity.UserBalance;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface UserBalanceRepository extends Repository<UserBalance, UUID> {
    @Query(value = """
        INSERT INTO user_balances (user_id, balance, updated_at)
        VALUES (:userId, :balance, timezone('utc', now()))
        ON CONFLICT (user_id)
        DO UPDATE SET
            balance = user_balances.balance + EXCLUDED.balance,
            updated_at = timezone('utc', now())
        RETURNING balance
        """, nativeQuery = true)
    BigDecimal upsertBalance(@Param("userId") UUID userId, @Param("balance") BigDecimal balanceDelta);
}
