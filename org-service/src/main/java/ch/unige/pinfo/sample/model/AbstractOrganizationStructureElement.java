package ch.unige.pinfo.sample.model;

import java.io.Serializable;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Cacheable
@Data()
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class AbstractOrganizationStructureElement extends PanacheEntity implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 8145079706851130795L;
    
    @Column(unique = true)
    String name;
    
    String address;
    
    String city;
    
    String postalCode;

}
