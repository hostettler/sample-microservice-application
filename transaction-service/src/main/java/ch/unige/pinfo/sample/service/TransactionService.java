package ch.unige.pinfo.sample.service;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
import ch.unige.pinfo.sample.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TransactionService {

    @Inject
    AccountService accountService;
    @Inject
    FxService fxService;

    public void execute(Transaction transaction) {
        Account sourceAccount = accountService.getAccount(transaction.sourceIBAN);
        JournalEntry firstEntry = JournalEntry.createEntry(sourceAccount, transaction.currency,
                fxService.getRate(transaction.currency, sourceAccount.currency), transaction.amount, JournalEntry.TransactionType.Debit);

        JournalEntry.persist(firstEntry);

        Account destAccount = accountService.getAccount(transaction.targetIBAN);
        JournalEntry secondEntry = JournalEntry.createEntry(destAccount, transaction.currency,
                fxService.getRate(transaction.currency, destAccount.currency), transaction.amount, JournalEntry.TransactionType.Credit);
        JournalEntry.persist(secondEntry);

    }

}