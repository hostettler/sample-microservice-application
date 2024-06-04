package ch.unige.pinfo.sample.model;

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
public class User extends PanacheEntityBase {

    public enum UserStatus {
        Active, Inactive
    }
    public enum Gender {
        Male, Female, NonBinary
    }    

    @Id
    @GeneratedValue
    private Long id;

    public String userId;
    public String firstName;
    public String lastName;
    public String city;
    public String postalCode;
    public String address;
    public LocalDate birth;
    public Gender gender;
    public UserStatus status;

}
