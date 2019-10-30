package com.sid.batch;

import com.sid.dao.BankTransactionRepository;
import com.sid.entities.BankTransaction;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BankTransactionItemWriter implements ItemWriter<BankTransaction> {
    @Autowired
    BankTransactionRepository bankTransactionRepository;
    @Override
    public void write(List<? extends BankTransaction> list) throws Exception {
            bankTransactionRepository.saveAll(list);
    }
}
