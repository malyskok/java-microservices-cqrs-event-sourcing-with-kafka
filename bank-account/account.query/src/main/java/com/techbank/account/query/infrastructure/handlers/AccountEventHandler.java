/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.account.query.domain.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler {

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .accountType(event.getAccountType())
                .createdDate(event.getCreatedDate())
                .balance(event.getOpeningBalance())
                .build();
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccountOpt = bankAccountRepository.findById(event.getId());
        if (bankAccountOpt.isEmpty()) {
            return;
        }
        var bankAccount = bankAccountOpt.get();
        bankAccount.setBalance(bankAccount.getBalance() + event.getAmount());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccountOpt = bankAccountRepository.findById(event.getId());
        if (bankAccountOpt.isEmpty()) {
            return;
        }
        var bankAccount = bankAccountOpt.get();
        bankAccount.setBalance(bankAccount.getBalance() - event.getAmount());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void on(AccountClosedEvent event) {
        bankAccountRepository.deleteById(event.getId());
    }
}