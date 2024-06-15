package ch.unige.pinfo.sample.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data()
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Organization extends AbstractOrganizationStructureElement {
    private static final long serialVersionUID = 7735123154617828327L;
    
    @OneToMany(mappedBy = "organization")
    @JsonManagedReference
    private List<BusinessEntity> businessEntities;
    
    public void addBusinessEntity(BusinessEntity entity) {
        if (this.businessEntities == null) {
            this.businessEntities = new ArrayList<>();
        }
        entity.setOrganization(this);
        this.businessEntities.add(entity);
    }
    
}
