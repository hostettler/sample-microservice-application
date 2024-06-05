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
public class Transaction extends PanacheEntityBase {

    public enum TransactionType {
        DEBIT, CREDIT
    }

    @Id
    @GeneratedValue
    Long id;

    String sourceIBAN;
    String targetIBAN;
    Double amount;
    String description;
    String currency;
    TransactionType type;

}
