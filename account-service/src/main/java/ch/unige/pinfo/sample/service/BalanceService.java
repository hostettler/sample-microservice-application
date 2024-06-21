package ch.unige.pinfo.sample.service;

import java.math.BigDecimal;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class BalanceService {

    private static final Logger LOG = Logger.getLogger(BalanceService.class);

    ConsistencyCheckService consistencyCheckService;
    
    @Inject
    BalanceService(ConsistencyCheckService consistencyCheckService) {
        this.consistencyCheckService = consistencyCheckService;
    }
    
    @Incoming("journal-update")
    @Transactional
    public void updateBalance(JournalEntry entry) {
        Account account = Account.find("iban", entry.getIban()).firstResult();
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        if (entry.getType() == JournalEntry.TransactionType.DEBIT) {
            account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(entry.getAmount())));
        } else if (entry.getType() == JournalEntry.TransactionType.CREDIT) {
            account.setBalance(account.getBalance().add(BigDecimal.valueOf(entry.getAmount())));
        }
    }

    @Transactional
    public void createAccount(Account account) {
        LOG.info("Create account " + account);
        LOG.info("Check account holder " + account.getAccountHolderUserId());
        if (!consistencyCheckService.checkUserExists(account.getAccountHolderUserId())) {
            throw new IllegalArgumentException(String.format("Account Holder %s does not exist", account.getAccountHolderUserId()));
        }
        
        LOG.info("Check account manager " + account.getAccountManagerUserId());
        if (!consistencyCheckService.checkUserExists(account.getAccountManagerUserId())) {
            throw new IllegalArgumentException(String.format("Account Manager %s does not exist", account.getAccountManagerUserId()));
        }
        
        LOG.info("Check branch " + account.getBranchId());
        if (!consistencyCheckService.checkOrganisationBranchId(account.getBranchId())) {
            throw new IllegalArgumentException(String.format("Branch %s does not exist", account.getBranchId()));
        }
        
        account.persist();
    }

    @Transactional
    public void updateBalance(String accountId, BigDecimal amount) {
        Account account = Account.find("iban", accountId).firstResult();
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        account.setBalance(account.getBalance().add(amount));
    }
}
