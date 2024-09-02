package ch.unige.pinfo.sample.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data()
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessEntity extends AbstractOrganizationStructureElement {

    private static final long serialVersionUID = 1180847886550924080L;
    
    @OneToMany(mappedBy = "businessEntity", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Branch> branches;

    @ManyToOne
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Organization organization;
    
    public void addBranch(Branch branch) {
        if (branches == null) {
            branches = new ArrayList<>();
        }
        branch.setBusinessEntity(this);
        branches.add(branch);
    }

}
