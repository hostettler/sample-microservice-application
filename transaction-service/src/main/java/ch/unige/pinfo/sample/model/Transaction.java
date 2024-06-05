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
        Debit, Credit
    }
    

    @Id
    @GeneratedValue
    private Long id;

    public String sourceIBAN;
    public String targetIBAN;
    public Double amount;
    public String description;
    public String currency;
    public TransactionType type;

}
