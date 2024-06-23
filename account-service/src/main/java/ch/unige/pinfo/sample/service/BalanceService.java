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
        LOG.debug(String.format("Update balance for entry %s", entry));
        Account account = Account.find("iban", entry.getIban()).firstResult();
        if (account == null) {
            LOG.error("Account not found for iban " + entry.getIban());
            throw new NotFoundException("Account not found");
        }
        LOG.debug(String.format("Update balance for account %s with %s", account.getIban(), entry.getAmount()));
        if (entry.getType() == JournalEntry.TransactionType.DEBIT) {
            BigDecimal balance = account.getBalance().subtract(BigDecimal.valueOf(entry.getAmount()));
            account.setBalance(balance);
            LOG.debug(String.format("New balance for account %s is %s", account.getIban(), balance));
        } else if (entry.getType() == JournalEntry.TransactionType.CREDIT) {
            BigDecimal balance = account.getBalance().add(BigDecimal.valueOf(entry.getAmount()));
            account.setBalance(balance);
            LOG.debug(String.format("New balance for account %s is %s", account.getIban(), balance));
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

}
