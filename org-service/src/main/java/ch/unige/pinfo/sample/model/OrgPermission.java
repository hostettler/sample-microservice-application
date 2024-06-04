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
public class OrgPermission extends PanacheEntity {

    public enum PERMISSION {
        NONE,
        REDACTED,
        READ_ONLY,
        READ_WRITE,
    }
    
    public String userId;
    public AbstractOrganizationStructureElement org;
    public PERMISSION permission;
}
