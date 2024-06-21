package com.unige.pinfo.sample.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.Transaction;
import ch.unige.pinfo.sample.service.BalanceService;
import ch.unige.pinfo.sample.service.ConsistencyCheckService;
import ch.unige.pinfo.sample.service.TransactionService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
class TransactionServiceTest {

    @Inject
    ConsistencyCheckService consistencyCheckService;

    @Inject
    BalanceService balanceService;
    @Inject
    TransactionService transactionService;    

    @BeforeEach
    void init() {
        consistencyCheckService.updateUserIds("1234");
        consistencyCheckService.updateUserIds("1234XXXXX");
        consistencyCheckService.updateUserIds("6789");
        
        consistencyCheckService.updateOrgIds("4567");
    }

    @Test
    void testExecuteTransactionService() throws InterruptedException {

        Account account = new Account();
        account.setAccountHolderUserId("1234");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("123456789");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal(100.0d));
        account.setInterest(new BigDecimal(0.0d));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        balanceService.createAccount(account);

        account = new Account();
        account.setAccountHolderUserId("1234XXXXX");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("1234XXXXX");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal(800.0d));
        account.setInterest(new BigDecimal(0.0d));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.SAVING);
        balanceService.createAccount(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setSourceIBAN("123456789");
        transaction.setTargetIBAN("1234XXXXX");
        transaction.setCurrency("CHF");
        transaction.setTransactionDate(LocalDate.now());

        transactionService.execute(transaction);

        Thread.sleep(2000);

        Account sourceAccount = Account.find("iban", "123456789").firstResult();
        Account targetAccount = Account.find("iban", "1234XXXXX").firstResult();

        Assertions.assertEquals(0d, sourceAccount.getBalance().doubleValue());
        Assertions.assertEquals(900d, targetAccount.getBalance().doubleValue());

        transaction.setSourceIBAN("XXXXXXXXX");
        transaction.setTargetIBAN("1234XXXXX");
        assertThrows(NotFoundException.class, () -> transactionService.execute(transaction));

        transaction.setSourceIBAN("123456789");
        transaction.setTargetIBAN("XXXXXXXXX");
        assertThrows(NotFoundException.class, () -> transactionService.execute(transaction));

    }

    @Test
    void testUnknownAccountsExecuteTransactionService() throws InterruptedException {

        Account account = new Account();
        account.setAccountHolderUserId("1234");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("123456789WW");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal(100.0d));
        account.setInterest(new BigDecimal(0.0d));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        balanceService.createAccount(account);

        account = new Account();
        account.setAccountHolderUserId("1234XXXXX");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("1234XXXXXWW");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal(800.0d));
        account.setInterest(new BigDecimal(0.0d));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.SAVING);
        balanceService.createAccount(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setSourceIBAN("123456789");
        transaction.setTargetIBAN("1234XXXXX");
        transaction.setCurrency("CHF");
        transaction.setTransactionDate(LocalDate.now());

        transaction.setSourceIBAN("DDDDD");
        transaction.setTargetIBAN("1234XXXXXWW");
        assertThrows(NotFoundException.class, () -> transactionService.execute(transaction));

        transaction.setSourceIBAN("123456789WW");
        transaction.setTargetIBAN("DDDDD");
        assertThrows(NotFoundException.class, () -> transactionService.execute(transaction));

    }
}
