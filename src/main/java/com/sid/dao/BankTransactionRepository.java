package com.sid.dao;

import com.sid.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BankTransactionRepository extends JpaRepository<BankTransaction,Long> {
}
