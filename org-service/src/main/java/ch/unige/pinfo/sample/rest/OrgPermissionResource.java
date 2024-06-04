package ch.unige.pinfo.sample.rest;

import java.util.List;

import ch.unige.pinfo.sample.model.OrgPermission;
import io.quarkus.oidc.UserInfo;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/permissions")
@Authenticated
public class OrgPermissionResource {

    @Inject
    UserInfo userInfo;
    @Inject
    SecurityIdentity identity;
    
    @GET    
    @Path("/{permissionId}")
    @RolesAllowed("admin")
    public OrgPermission get(String permissionId) {
        return OrgPermission.findById(permissionId);
    }
    
    @GET    
    @Path("/org/{orgId}")
    @RolesAllowed("admin")
    public List<OrgPermission> getOrgPermission(String orgId) {
        return OrgPermission.list("userId", userInfo.getEmail());
    }
}