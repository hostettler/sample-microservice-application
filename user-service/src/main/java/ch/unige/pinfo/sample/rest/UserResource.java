package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.User;
import ch.unige.pinfo.sample.service.UserService;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/users")
public class UserResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    private UserService userService;

    @GET
    @RolesAllowed("user")
    @Path("/me")
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @Path("/ids")
    @RolesAllowed({ "admin"})
    public List<String> getIds() {
        LOG.info("List all user Ids");
        return User.find("select userId from User").project(String.class).list();
    }
    
    @GET
    @Path("/all")
    @RolesAllowed({ "admin", "user" })
    public List<User> list() {
        LOG.info("List all users");
        return User.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{customerId}")
    @RolesAllowed({ "admin", "user" })
    public User get(@PathParam("customerId") String customerId) {
        return User.findById(customerId);
    }

    @POST
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "user" })
    public User add(User user) {
        LOG.infof("Create user with userId : %s", user.getUserId());
        userService.create(user);        
        return user;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "user" })
    public User update(User user) {

        LOG.infof("Principal user name for the logged user : %s", securityIdentity.getPrincipal().getName());
        LOG.infof("Roles for the logged user : %s", securityIdentity.getRoles());
        if (!securityIdentity.hasRole("admin") && !securityIdentity.getPrincipal().getName().equals(user.getUserId())) {
            throw new NotAuthorizedException("User Id does not match the authenticated user.");
        }

        User entity = User.findById(user.getId());
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setBirth(user.getBirth());
        entity.setAddress(user.getAddress());
        entity.setGender(user.getGender());
        entity.setCity(user.getCity());
        entity.setPostalCode(user.getPostalCode());
        entity.setFirstName(user.getFirstName());

        return user;
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public void delete(Long id) {
        userService.disableUser(id);
    }
}