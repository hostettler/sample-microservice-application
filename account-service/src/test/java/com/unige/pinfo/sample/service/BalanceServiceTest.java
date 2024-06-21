package com.unige.pinfo.sample.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.service.BalanceService;
import ch.unige.pinfo.sample.service.ConsistencyCheckService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
class BalanceServiceTest {

    @Inject
    ConsistencyCheckService consistencyCheckService;
    
    @Inject
    BalanceService balanceService;
    @BeforeEach
    void init() {
        consistencyCheckService.updateUserIds("1234");
        consistencyCheckService.updateUserIds("6789");
        consistencyCheckService.updateOrgIds("4567");
    }

    @Test
    void testUpdateBalance() {
        Account account = new Account();
        account.setAccountHolderUserId("1234");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("9101112");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal("0.0"));
        account.setInterest(new BigDecimal("0.0"));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        System.out.println("Account to create : " + account);
        balanceService.createAccount(account);
        System.out.println("Account created : " + account);

        balanceService.updateBalance("9101112", new BigDecimal("100.0"));

        account = Account.findById(account.getId());
        Assertions.assertEquals(new BigDecimal("100.00"), account.getBalance());

        assertThrows(NotFoundException.class, () -> balanceService.updateBalance("XXXXX", new BigDecimal("100.0")));
        
    }
}
