package ch.unige.pinfo.sample.rest;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Branch;
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

@Path("/branches")
public class BranchResource {

    @GET
    @Path("/all")
    public List<Branch> list() {
        return Branch.listAll(Sort.by("id"));
    }

    @GET
    @Path("/ids")
    @RolesAllowed({ "admin" })
    public List<Long> getIds() {
        return Branch.find("select id from Branch").project(Long.class).list();
    }

    @GET
    @Path("/{branchId}")
    public Branch get(@PathParam("branchId") String branchId) {
        return Branch.findById(branchId);
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" })
    public Branch add(Branch branch) {
        Branch.persist(branch);
        return branch;
    }

    @PUT
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" })
    public Branch update(Branch branch) {

        Branch entity = Branch.findById(branch.id);
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.setAddress(branch.getAddress());
        entity.setCity(branch.getCity());
        entity.setPostalCode(branch.getPostalCode());

        return branch;
    }

}