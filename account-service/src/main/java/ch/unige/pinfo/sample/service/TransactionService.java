package ch.unige.pinfo.sample.service;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
import ch.unige.pinfo.sample.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class TransactionService {
    private static final Logger LOG = Logger.getLogger(TransactionService.class);

    FxService fxService;

    @Channel("journal-update")
    Emitter<JournalEntry> jeEmmitter;

    @Inject
    public TransactionService(FxService fxService) {
        this.fxService = fxService;
    }

    @Transactional
    public void execute(Transaction transaction) {
        LOG.debug(String.format("Execute transaction %s", transaction));
        Account sourceAccount = Account.find("iban", transaction.getSourceIBAN()).firstResult();
        if (sourceAccount == null) {
            throw new NotFoundException("Source account not found");
        }
        Account destAccount = Account.find("iban", transaction.getTargetIBAN()).firstResult();
        if (destAccount == null) {
            throw new NotFoundException("Target account not found");
        }

        JournalEntry firstEntry = JournalEntry.createEntry(sourceAccount, transaction.getCurrency(),
                fxService.getRate(transaction.getCurrency(), sourceAccount.getCurrency()), transaction.getAmount(),
                JournalEntry.TransactionType.DEBIT);
        LOG.debug(String.format("First entry %s", firstEntry));
        JournalEntry secondEntry = JournalEntry.createEntry(destAccount, transaction.getCurrency(),
                fxService.getRate(transaction.getCurrency(), destAccount.getCurrency()), transaction.getAmount(),
                JournalEntry.TransactionType.CREDIT);
        LOG.debug(String.format("Second entry %s", secondEntry));
        firstEntry.persist();
        jeEmmitter.send(firstEntry);
        secondEntry.persist();
        jeEmmitter.send(secondEntry);

    }

}