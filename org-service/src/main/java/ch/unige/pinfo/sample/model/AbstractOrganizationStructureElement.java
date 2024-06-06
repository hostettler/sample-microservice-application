package ch.unige.pinfo.sample.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Cacheable
@Data()
@EqualsAndHashCode(callSuper = false)
@Entity
public abstract class AbstractOrganizationStructureElement extends PanacheEntity {
    String name;
    String address;
    String city;
    String postalCode;

}
