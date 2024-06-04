package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Branch;
import ch.unige.pinfo.sample.model.BusinessEntity;
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

@Path("/business-entity")
public class BusinessEntityResource {

    private static final Logger LOG = Logger.getLogger(BusinessEntityResource.class);

    @GET
    @Path("/all")
    public List<BusinessEntity> list() {
        return BusinessEntity.listAll(Sort.by("id"));
    }
    
    @GET
    @Path("/{branchId}")
    public BusinessEntity get(String businessEntityId) {
        return BusinessEntity.findById(businessEntityId);
    }

    @POST
    @Transactional 
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BusinessEntity add(BusinessEntity businessEntity) {
        BusinessEntity.persist(businessEntity);
        return businessEntity;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
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

    @DELETE
    @Transactional 
    @Path("/{id}")    
    public void delete(Long id) {
        LOG.infof("delete branch by id : {}", id);
        Branch.deleteById(id);
    }
}