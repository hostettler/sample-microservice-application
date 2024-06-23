package com.unige.pinfo.sample.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
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
    void testUpdateBalanceCredit() {
        Account account = new Account();
        account.setAccountHolderUserId("1234");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("9101112U");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal("1000.0"));
        account.setInterest(new BigDecimal("0.0"));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        balanceService.createAccount(account);

        JournalEntry entry = new JournalEntry();
        entry.setIban(account.getIban());
        entry.setAmount(100.0);
        entry.setType(JournalEntry.TransactionType.CREDIT);
        entry.setOriginalCurrency("CHF");

        balanceService.updateBalance(entry);

        account = balanceService.getAccount("9101112U");
        Assertions.assertEquals(new BigDecimal("1100.00"), account.getBalance());

    }

    @Test
    void testUpdateBalanceDebit() {
        Account account = new Account();
        account.setAccountHolderUserId("1234");
        account.setBranchId("4567");
        account.setAccountManagerUserId("6789");
        account.setIban("42345232342");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal("1000.0"));
        account.setInterest(new BigDecimal("0.0"));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        balanceService.createAccount(account);

        JournalEntry entry = new JournalEntry();
        entry.setIban(account.getIban());
        entry.setAmount(100.0);
        entry.setType(JournalEntry.TransactionType.DEBIT);
        entry.setOriginalCurrency("CHF");

        balanceService.updateBalance(entry);

        account = Account.find("iban", "42345232342").firstResult();
        Assertions.assertEquals(new BigDecimal("900.00"), account.getBalance());

    }

    @Test
    void testUpdateBalanceOfUnknownAccount() {
        final JournalEntry entry = new JournalEntry();
        entry.setIban("XXXX");
        entry.setAmount(100.0);
        entry.setType(JournalEntry.TransactionType.CREDIT);
        entry.setOriginalCurrency("CHF");

        assertThrows(NotFoundException.class, () -> balanceService.updateBalance(entry));
    }

    @ParameterizedTest
    @MethodSource("multipleUserAndBranchProvider")
    void testCreateAccountWitUknownAccountHolder(String userId, String branchId, String managerId) {
        Account account = new Account();
        account.setAccountHolderUserId(userId);
        account.setBranchId(branchId);
        account.setAccountManagerUserId(managerId);
        account.setIban("42345232342");
        account.setCreationDate(LocalDate.now());
        account.setBalance(new BigDecimal("1000.0"));
        account.setInterest(new BigDecimal("0.0"));
        account.setBalanceUpdatedDate(LocalDate.now());
        account.setCurrency("CHF");
        account.setType(Account.Type.CHECKING);
        assertThrows(IllegalArgumentException.class, () -> balanceService.createAccount(account));        
    }
        
    static Stream<Arguments> multipleUserAndBranchProvider() {
        return Stream.of(
            Arguments.arguments("1234XX", "4567", "6789"),
            Arguments.arguments("1234", "4567XX", "6789"),
            Arguments.arguments("1234", "4567", "6789XX")
        );
    }
}
