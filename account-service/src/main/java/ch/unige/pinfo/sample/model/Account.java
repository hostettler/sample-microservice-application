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
        ACTIVE, INACTIVE
    }

    public enum Type {
        CHECKING, SAVING
    }

    @Id
    @GeneratedValue
    Long id;

    String accountHolderUserId;
    String branchId;
    String accountManagerUserId;
    String iban;
    LocalDate creationDate;
    BigDecimal balance;
    BigDecimal interest;
    LocalDate balanceUpdatedDate;
    String currency;
    Type type;
    Status status;

}
