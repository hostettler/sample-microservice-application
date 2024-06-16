package ch.unige.pinfo.sample.rest;

import java.util.List;

import ch.unige.pinfo.sample.model.OrgPermission;
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

    UserInfo userInfo;
    SecurityIdentity identity;

    @Inject
    public OrgPermissionResource(UserInfo userInfo, SecurityIdentity identity) {
        this.userInfo = userInfo;
        this.identity = identity;
    }
    
    @GET    
    @Path("/me")
    @RolesAllowed("admin")
    public List<OrgPermission> getOrgPermission() {
        return OrgPermission.list("userId", userInfo.getEmail());
    }
}