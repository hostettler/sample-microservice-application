package ch.unige.pinfo.sample.service;

import java.math.BigDecimal;

import ch.unige.pinfo.sample.model.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BalanceService {

    @Transactional
    public void updateBalance(String accountId, BigDecimal amount) {
        Account account = Account.findById(accountId, LockModeType.PESSIMISTIC_WRITE);
        account.setBalance(account.getBalance().add(amount));
        Account.persist(account);
    }
}
