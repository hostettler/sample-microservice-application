package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Branch;
import ch.unige.pinfo.sample.model.BusinessEntity;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/business-entities")
public class BusinessEntityResource {

    private static final Logger LOG = Logger.getLogger(BusinessEntityResource.class);

    @GET
    @Path("/all")
    public List<BusinessEntity> list() {
        return BusinessEntity.listAll(Sort.by("id"));
    }
    

    @GET
    @Path("/ids")
    @RolesAllowed({ "admin"})
    public List<Long> getIds() {
        LOG.info("List all branch Ids");
        return BusinessEntity.find("select id from BusinessEntity").project(Long.class).list();
    }
    
    @GET
    @Path("/{businessEntityId}")
    public BusinessEntity get(@PathParam("businessEntityId") String businessEntityId) {
        return BusinessEntity.findById(businessEntityId);
    }

    @POST
    @Transactional 
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
    public BusinessEntity add(BusinessEntity businessEntity) {
        BusinessEntity.persist(businessEntity);
        return businessEntity;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
    public BusinessEntity update(BusinessEntity businessEntity) {
        BusinessEntity entity = BusinessEntity.findById(businessEntity.id);
        if(entity == null) {
            throw new NotFoundException();
        }

        entity.setAddress(businessEntity.getAddress());
        entity.setCity(businessEntity.getCity());
        entity.setPostalCode(businessEntity.getPostalCode());

        return businessEntity;
    }
    
    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin"})
    @Path("/add-branch/{businessEntityId}/{branchId}")
    public void addBranch(long businessEntityId, long branchId) {
        BusinessEntity entity = BusinessEntity.findById(businessEntityId);
        if(entity == null) {
            throw new NotFoundException();
        }
        Branch branch = Branch.findById(branchId);
        if(branch == null) {
            throw new NotFoundException();
        }
        entity.addBranch(branch);
    }

    @DELETE
    @Transactional 
    @Path("/{id}")    
    @RolesAllowed({ "admin"})
    public void delete(Long id) {
        LOG.infof("delete branch by id : {}", id);
        Branch.deleteById(id);
    }
}