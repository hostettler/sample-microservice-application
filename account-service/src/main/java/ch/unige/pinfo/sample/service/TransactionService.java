package ch.unige.pinfo.sample.service;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
import ch.unige.pinfo.sample.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TransactionService {


    @Inject
    FxService fxService;

    public void execute(Transaction transaction) {
        Account sourceAccount = Account.findById(transaction.getSourceIBAN());
        JournalEntry firstEntry = JournalEntry.createEntry(sourceAccount, transaction.getCurrency(),
                fxService.getRate(transaction.getCurrency(), sourceAccount.getCurrency()), transaction.getAmount(),
                JournalEntry.TransactionType.DEBIT);

        JournalEntry.persist(firstEntry);

        Account destAccount = Account.findById(transaction.getTargetIBAN());
        JournalEntry secondEntry = JournalEntry.createEntry(destAccount, transaction.getCurrency(),
                fxService.getRate(transaction.getCurrency(), destAccount.getCurrency()), transaction.getAmount(),
                JournalEntry.TransactionType.CREDIT);
        JournalEntry.persist(secondEntry);

    }

}