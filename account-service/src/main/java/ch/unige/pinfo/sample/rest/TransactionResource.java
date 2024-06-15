package ch.unige.pinfo.sample.rest;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Transaction;
import ch.unige.pinfo.sample.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/transactions")
public class TransactionResource {

    @Inject
    TransactionService transactionService;

    @POST
    @Transactional 
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void put(Transaction transaction) {
        transactionService.execute(transaction);
    }


}