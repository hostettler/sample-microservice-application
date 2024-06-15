package ch.unige.pinfo.sample.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data()
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Branch extends AbstractOrganizationStructureElement {

    private static final long serialVersionUID = 6858154808658868408L;

    @ManyToOne
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    BusinessEntity businessEntity;
    
}
