package ch.unige.pinfo.sample.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class Account extends PanacheEntityBase {

    public enum Status {
        Active, Inactive
    }
    public enum Type {
        checking, savings
    }    

    @Id
    @GeneratedValue
    private Long id;

    public String accountHolderUserId;
    public String branchId;
    public String accountManagerUserId;
    public String iban;
    public LocalDate creationDate;
    public BigDecimal balance;
    public BigDecimal interest;
    public LocalDate balanceUpdatedDate;
    public String currency;
    public Type type;
    public Status status;
    

}
