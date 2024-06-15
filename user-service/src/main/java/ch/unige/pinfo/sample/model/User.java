package ch.unige.pinfo.sample.model;

import java.time.LocalDate;

import ch.unige.pinfo.sample.util.PII;
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
public class User extends PanacheEntityBase {

    public enum UserStatus {
        ACTIVE, INACTIVE
    }

    public enum Gender {
        MALE, FEMALE, NON_BINARY
    }

    @Id
    @GeneratedValue
    private Long id;

    String userId;
    @PII
    String firstName;
    @PII
    String lastName;

    String city;
    String postalCode;
    @PII
    String address;
    @PII
    LocalDate birth;
    Gender gender;
    UserStatus status = UserStatus.ACTIVE;

}
