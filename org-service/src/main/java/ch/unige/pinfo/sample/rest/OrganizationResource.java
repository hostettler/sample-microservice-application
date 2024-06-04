package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Branch;
import ch.unige.pinfo.sample.model.Organization;
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

@Path("/organizations")
public class OrganizationResource {

    private static final Logger LOG = Logger.getLogger(OrganizationResource.class);

    @GET
    @Path("/all")
    public List<Organization> list() {
        return Organization.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{orgId}")
    public Organization get(String orgId) {
        return Organization.findById(orgId);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Organization add(Organization org) {
        Organization.persist(org);
        return org;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Organization update(Organization org) {
        Organization entity = Organization.findById(org.id);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setAddress(org.getAddress());
        entity.setCity(org.getCity());
        entity.setPostalCode(org.getPostalCode());

        return org;
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public void delete(Long id) {
        LOG.infof("delete user by id : {}", id);
        Branch.deleteById(id);
    }
}