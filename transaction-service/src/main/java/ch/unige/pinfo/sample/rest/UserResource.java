package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.User;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @GET
    @Path("/all")
    public List<User> list() {
        LOG.info("List all users");
        return User.listAll(Sort.by("id"));
    }
    
    @GET
    @Path("/{customerId}")
    public User get(String customerId) {
        return User.findById(customerId);
    }

    @POST
    @Transactional 
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User add(User user) {
        User.persist(user);
        return user;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
    public User update(User user) {
        User entity = User.findById(user.getId());
        if(entity == null) {
            throw new NotFoundException();
        }
        
        entity.setBirth(user.getBirth());
        entity.setAddress(user.getAddress());
        entity.setGender(user.getGender());
        entity.setCity(user.getCity());
        entity.setPostalCode(user.getPostalCode());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());

        return user;
    }

    @DELETE
    @Transactional 
    @Path("/{id}")    
    public void delete(Long id) {
        LOG.infof("delete user by id : {}", id);
        User.deleteById(id);
    }
}