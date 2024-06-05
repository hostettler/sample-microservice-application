package ch.unige.pinfo.sample.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Cacheable
@Data()
@EqualsAndHashCode(callSuper = false)
@Entity
public class JournalEntry extends PanacheEntityBase {

    
    
    public enum TransactionType {
        Debit, Credit
    }
    

    @Id
    @GeneratedValue
    private Long id;

    public String iban;
    public Double amount;
    public String description;
    public String originalCurrency;
    public String accountCurrency;
    public TransactionType type;

        public static JournalEntry createEntry(Account sourceAccount, String trxCurrency, Double rate, 
                Double amount, TransactionType type) {
        JournalEntry e = new JournalEntry();
        e.accountCurrency = sourceAccount.currency;
        e.originalCurrency = trxCurrency;
        if (trxCurrency.equals(sourceAccount.currency)) {
            e.amount = e.amount;
        } else {
            e.amount = e.amount * rate;
        }
        e.iban = sourceAccount.accountId;
        e.type = type;
        return e;
    }
}
