/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.techbank.account.query.api.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.account.query.domain.BankAccountRepository;
import com.techbank.cqrs.core.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountQueryHandler implements QueryHandler {

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Override
    public List<BaseEntity> handle(FindAllAccountsQuery query) {
        return StreamSupport.stream(bankAccountRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseEntity> handle(FindAccountByIdQuery query) {
        Optional<BankAccount> account = bankAccountRepository.findById(query.getId());
        if (account.isEmpty()) {
            return new ArrayList<>();
        }
        return Collections.singletonList(account.get());
    }

    @Override
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountHolder(query.getHolder());
        if (account.isEmpty()) {
            return new ArrayList<>();
        }
        return Collections.singletonList(account.get());
    }

    @Override
    public List<BaseEntity> handle(FindAccountWithBalanceQuery query) {
        return switch (query.getEqualityType()) {
            case GREATER_THAN -> bankAccountRepository.findByBalanceGreaterThan(query.getBalance());
            case LESS_THAN -> bankAccountRepository.findByBalanceLessThan(query.getBalance());
        };
    }
}