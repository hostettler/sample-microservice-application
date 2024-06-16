package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.BusinessEntity;
import ch.unige.pinfo.sample.model.Organization;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/organizations")
public class OrganizationResource {


    @GET
    @Path("/all")
    public List<Organization> list() {
        return Organization.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{orgId}")
    public Organization get(@PathParam("orgId") String orgId) {
        return Organization.findById(orgId);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
    public Organization add(Organization org) {
        Organization.persist(org);
        return org;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
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

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
    @Path("/add-business-entity/{orgId}/{businessEntityId}")
    public void addBusinessEntity(long orgId, long businessEntityId) {
        Organization org = Organization.findById(orgId);
        if(org == null) {
            throw new NotFoundException();
        }
        BusinessEntity be = BusinessEntity.findById(businessEntityId);
        if(be == null) {
            throw new NotFoundException();
        }
        org.addBusinessEntity(be);
    }
    
}